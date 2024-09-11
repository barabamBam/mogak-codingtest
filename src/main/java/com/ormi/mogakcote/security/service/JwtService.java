package com.ormi.mogakcote.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ormi.mogakcote.security.RandomStringGenerator;
import com.ormi.mogakcote.security.model.AccessTokenWrapper;
import com.ormi.mogakcote.security.model.AuthorizeToken;
import com.ormi.mogakcote.redis.service.RedisService;
import com.ormi.mogakcote.exception.auth.AuthInvalidException;
import com.ormi.mogakcote.exception.dto.ErrorType;
import com.ormi.mogakcote.user.application.UserService;
import com.ormi.mogakcote.user.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

import static com.ormi.mogakcote.security.TokenConstants.*;

@Slf4j
@Service
public class JwtService {

    private final UserService userService;
    private final RedisService<String> redisService;
    private final String secretKey;
    private final JwtParser secretParser;
    private final ObjectMapper objectMapper;

    @Value("${spring.security.jwt.issuer}")
    private String issuerUrl;

    @Value("${spring.security.jwt.access-token-expiration-second}")
    private int accessTokenExpirationSecond;

    @Value("${spring.security.jwt.refresh-token-expiration-second}")
    private int refreshTokenExpirationSecond;

    @Autowired
    public JwtService(UserService userService, RedisService<String> redisService, @Value("${spring.security.jwt.secretKey}") String secretKey, ObjectMapper objectMapper) {
        this.userService = userService;
        this.redisService = redisService;
        this.secretKey = secretKey;
        this.objectMapper = objectMapper;
        this.secretParser = Jwts.parser().verifyWith(getSignedKey()).build();
    }

    @Nullable
    public Long getUserId(String accessToken) {
        try {
            Jws<Claims> claims = secretParser.parseSignedClaims(accessToken);
            return claims.getPayload().get(USER_ID_CLAIM, Long.class);
        } catch (JwtException e) {
            return null;
        } catch (Exception e) {
            log.warn("Unexpected exception threw while read claim", e);
            throw e;
        }
    }

    public String generateAccessToken(User user) {
        Instant issuedAt = Instant.now();
        Instant expiredAt = issuedAt.plusSeconds(accessTokenExpirationSecond);

        ClaimsBuilder claimsBuilder = Jwts.claims();
        claimsBuilder.subject(user.getName());
        claimsBuilder.add(USER_ID_CLAIM, user.getId());
        claimsBuilder.add(USER_EMAIL_CLAIM, user.getEmail());
        claimsBuilder.add(USER_AUTHORITY_CLAIM, user.getAuthority().toString());
        claimsBuilder.issuedAt(Date.from(issuedAt));
        claimsBuilder.notBefore(Date.from(issuedAt));
        claimsBuilder.issuer(issuerUrl);
        claimsBuilder.expiration(Date.from(expiredAt));
        Claims claims = claimsBuilder.build();
        return Jwts.builder().claims(claims).signWith(getSignedKey()).compact();
    }

    public String getRefreshToken() {
        final int refreshTokenLength = 16;
        return RandomStringGenerator.generateRandomString(refreshTokenLength);
    }

    public void setTokenPair(AuthorizeToken authorizeToken) {
        redisService.set(getPairTokenKey(authorizeToken.getRefreshToken()), authorizeToken.getAccessToken(), refreshTokenExpirationSecond);
    }

    public String refresh(AuthorizeToken authorizeToken) {
        final String accessToken = authorizeToken.getAccessToken();
        final String refreshToken = authorizeToken.getRefreshToken();

        if (isUsedAccessToken(accessToken)) {
            throw new AuthInvalidException(ErrorType.USED_ACCESS_TOKEN_ERROR);
        }

        if (isUsedRefreshToken(refreshToken)) {
            throw new AuthInvalidException(ErrorType.USED_REFRESH_TOKEN_ERROR);
        }

        String storedAccessToken = redisService.getValue(getPairTokenKey(refreshToken));
        if (!accessToken.equals(storedAccessToken)) {
            throw new AuthInvalidException(ErrorType.REFRESH_ACCESS_TOKEN_NOT_MATCH_ERROR);
        }

        Claims claims;
        try {
            claims = secretParser.parseSignedClaims(accessToken).getPayload();
        } catch (JwtException e) {
            if (e instanceof ExpiredJwtException expired) {
                claims = expired.getClaims();
            } else {
                throw new AuthInvalidException(ErrorType.INVALID_ACCESS_TOKEN_ERROR);
            }
        }

        storeUsedAccessToken(accessToken);
        storeUsedRefreshToken(refreshToken);
        Long userId = claims.get(USER_ID_CLAIM, Long.class);
        User user = userService.getById(userId);
        return generateAccessToken(user);
    }

    @Nullable
    public Claims validateAccessToken(String accessToken) {
        try {
            if (isUsedAccessToken(accessToken)) {
                throw new AuthInvalidException(ErrorType.USED_ACCESS_TOKEN_ERROR);
            }
            return secretParser.parseSignedClaims(accessToken).getPayload();
        } catch (JwtException e) {
            return null;
        }
    }

    public void writeResponse(HttpServletResponse response, AuthorizeToken authorizeToken) throws IOException {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, authorizeToken.getRefreshToken());
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(refreshTokenExpirationSecond);
        cookie.setPath("/");
        response.addCookie(cookie);

        AccessTokenWrapper wrapper = new AccessTokenWrapper(authorizeToken.getAccessToken());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), wrapper);
    }

    public void remove(HttpServletRequest request, HttpServletResponse response) {
        Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(REFRESH_TOKEN_COOKIE_NAME))
                .findFirst()
                .ifPresent(c -> {
                    storeUsedRefreshToken(c.getValue());
                    redisService.delete(getPairTokenKey(c.getValue()));
                });

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    private boolean isUsedAccessToken(String accessToken) {
        return redisService.contains(getUsedAccessTokenKey(accessToken));
    }

    private boolean isUsedRefreshToken(String refreshToken) {
        return redisService.contains(getUsedRefreshTokenKey(refreshToken));
    }

    private void storeUsedAccessToken(String accessToken) {
        redisService.set(getUsedAccessTokenKey(accessToken), "", refreshTokenExpirationSecond);
    }

    private void storeUsedRefreshToken(String refreshToken) {
        redisService.set(getUsedRefreshTokenKey(refreshToken), "", refreshTokenExpirationSecond);
    }

    private String getUsedAccessTokenKey(String accessToken) {
        return "used_a_" + accessToken.hashCode();
    }

    private String getUsedRefreshTokenKey(String refreshToken) {
        return "used_r_" + refreshToken;
    }

    private String getPairTokenKey(String refreshToken) {
        return "pair_" + refreshToken;
    }

    private SecretKey getSignedKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
