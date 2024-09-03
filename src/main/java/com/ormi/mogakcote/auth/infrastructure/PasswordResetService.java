package com.ormi.mogakcote.auth.infrastructure;

import com.ormi.mogakcote.exception.dto.ErrorType;
import com.ormi.mogakcote.exception.user.UserInvalidException;
import com.ormi.mogakcote.redis.service.RedisService;
import com.ormi.mogakcote.security.RandomStringGenerator;
import com.ormi.mogakcote.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    @Value("${spring.security.user.password.reset.registerTimeoutSecond}")
    private int registerTimeoutSecond;

    private final RedisService<String> redisService;
    private final UserService userService;

    public String register(String email) {
        if (!userService.existsByEmail(email)) {
            throw new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR);
        }

        String key = getKey(email);
        String code = RandomStringGenerator.generateRandomString(8);
        redisService.set(key, code, registerTimeoutSecond);

        return code;
    }

    public String reset(String email, String code) {
        if (code == null) {
            throw new IllegalArgumentException();
        }

        String key = getKey(email);
        String value = redisService.getValue(key);
        if (!code.equals(value)) {
            throw new UserInvalidException(ErrorType.RESET_PASSWORD_CODE_NOT_MATCH_ERROR);
        }

        redisService.delete(key);
        String newPassword = RandomStringGenerator.generateRandomString(24);
        userService.updatePassword(email, newPassword);
        return newPassword;
    }

    private String getKey(String email) {
        return "Preset_" + email;
    }
}
