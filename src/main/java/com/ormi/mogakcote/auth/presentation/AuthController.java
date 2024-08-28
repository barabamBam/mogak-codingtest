package com.ormi.mogakcote.auth.presentation;

import com.ormi.mogakcote.auth.dto.request.LoginRequest;
import com.ormi.mogakcote.exception.auth.AuthInvalidException;
import com.ormi.mogakcote.exception.dto.ErrorType;
import com.ormi.mogakcote.security.TokenConstants;
import com.ormi.mogakcote.security.model.AccessTokenWrapper;
import com.ormi.mogakcote.security.service.JwtService;
import com.ormi.mogakcote.security.model.AuthorizeToken;
import com.ormi.mogakcote.user.application.UserService;
import com.ormi.mogakcote.user.domain.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @SneakyThrows
    @PostMapping("/login")
    public void login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        User user = userService.getByEmail(loginRequest.getEmail());
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthInvalidException(ErrorType.WRONG_PASSWORD_ERROR);
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.getRefreshToken();
        AuthorizeToken authorizeToken = new AuthorizeToken(accessToken, refreshToken);
        jwtService.setTokenPair(authorizeToken);
        jwtService.writeResponse(response, authorizeToken);
    }

    @SneakyThrows
    @PostMapping("/refresh")
    public void refresh(@RequestBody AccessTokenWrapper accessToken, @RequestHeader(name = TokenConstants.REFRESH_TOKEN_COOKIE_NAME) String refreshToken, HttpServletResponse response) {
        AuthorizeToken oldAuthorizeToken = new AuthorizeToken(accessToken.getAccessToken(), refreshToken);
        String newAccessToken = jwtService.refresh(oldAuthorizeToken);
        String newRefreshToken = jwtService.getRefreshToken();
        AuthorizeToken authorizeToken = new AuthorizeToken(newAccessToken, newRefreshToken);
        jwtService.setTokenPair(authorizeToken);
        jwtService.writeResponse(response, authorizeToken);
    }
}
