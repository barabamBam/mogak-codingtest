package com.ormi.mogakcote.auth.presentation;

import com.ormi.mogakcote.auth.dto.request.EmailWrapper;
import com.ormi.mogakcote.auth.dto.request.FindEmailRequest;
import com.ormi.mogakcote.auth.dto.request.LoginRequest;
import com.ormi.mogakcote.auth.dto.request.PasswordResetRequest;
import com.ormi.mogakcote.auth.dto.response.FindEmailResponse;
import com.ormi.mogakcote.auth.infrastructure.PasswordResetService;
import com.ormi.mogakcote.email.service.EmailService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordResetService resetService;
    private final EmailService emailService;
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
    public void refresh(@RequestBody AccessTokenWrapper accessToken, @CookieValue(name = TokenConstants.REFRESH_TOKEN_COOKIE_NAME) String refreshToken, HttpServletResponse response) {
        AuthorizeToken oldAuthorizeToken = new AuthorizeToken(accessToken.getAccessToken(), refreshToken);
        String newAccessToken = jwtService.refresh(oldAuthorizeToken);
        String newRefreshToken = jwtService.getRefreshToken();
        AuthorizeToken authorizeToken = new AuthorizeToken(newAccessToken, newRefreshToken);
        jwtService.setTokenPair(authorizeToken);
        jwtService.writeResponse(response, authorizeToken);
    }

    @SneakyThrows
    @GetMapping("/email")
    public ResponseEntity<FindEmailResponse> findEmail(FindEmailRequest emailRequest) {
        String email = userService.getEmailByNameAndNickname(emailRequest.getName(), emailRequest.getNickname());
        FindEmailResponse response = new FindEmailResponse(email);
        return ResponseEntity.ok(response);
    }

    @SneakyThrows
    @PostMapping("/reset-password-mail")
    public ResponseEntity<Void> sendPasswordResetMail(@RequestBody EmailWrapper emailWrapper) {
        String code = resetService.register(emailWrapper.getEmail());
        emailService.send("noreply@noreply.com", emailWrapper.getEmail(), "비밀번호 초기화 요청", getResetFormHtml(emailWrapper.getEmail(), code), true);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(PasswordResetRequest resetRequest) {
        String newPassword = resetService.reset(resetRequest.getEmail(), resetRequest.getCode());
        emailService.send("noreply@noreply.com", resetRequest.getEmail(), "비밀번호 초기화 완료", "비밀번호: " + newPassword, false);
        return ResponseEntity.ok().build();
    }

    private String getResetFormHtml(String email, String code) {
        final String url = MvcUriComponentsBuilder.fromMethodName(AuthController.class, "resetPassword", new Object()).build().toUriString();
        final String format = """
                <form action="%s" method="post">
                    <p>비밀번호를 초기화 하려면 아래 버튼을 클릭하세요</p>
                    <input type="hidden" name="email" value="%s">
                    <input type="hidden" name="code" value="%s">
                    <button style="background-color: #0d6efd;
                                                     border:  none;
                                                     border-radius: 5px;
                                                     padding: 5px;
                                                     color: white;
                                                     font-family: sans-serif;
                                                     font-weight: 700;">비밀번호 초기화</button>
                </form>
                """;

        return format.formatted(url, email, code);
    }
}
