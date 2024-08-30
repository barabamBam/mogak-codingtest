package com.ormi.mogakcote.user.presentation;

import com.ormi.mogakcote.common.model.ResponseDto;
import com.ormi.mogakcote.user.application.UserService;
import com.ormi.mogakcote.user.dto.request.PasswordRequest;
import com.ormi.mogakcote.user.dto.request.RegisterRequest;

import com.ormi.mogakcote.user.dto.response.ValidatePasswordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping("/users/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam String username) {
        var response = userService.checkNickname(username);
        return ResponseDto.ok(response);
    }

    @GetMapping("/users/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        var response = userService.existsByEmail(email);
        return ResponseDto.ok(response);

    }

    @PostMapping("/signup/validate-password")
    public ResponseEntity<ValidatePasswordResponse> validatePassword(@RequestBody PasswordRequest request) {
        boolean isValid = userService.validatePassword(request.getPassword());
        String error = isValid ? null : "비밀번호는 8자 이상, 소문자+숫자만 가능합니다.";
        return ResponseEntity.ok(new ValidatePasswordResponse(isValid, error));
    }

    @PostMapping("/users/register")
    public ResponseEntity<?> registerUser(
            @RequestBody RegisterRequest request
    ) {
        var response = userService.registerUser(request);
        return ResponseDto.created(response);
    }
}