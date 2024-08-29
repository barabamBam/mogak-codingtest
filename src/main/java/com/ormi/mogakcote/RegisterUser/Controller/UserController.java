package com.ormi.mogakcote.RegisterUser.Controller;

import com.ormi.mogakcote.RegisterUser.dto.*;
import com.ormi.mogakcote.RegisterUser.entity.User;
import com.ormi.mogakcote.RegisterUser.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/check-nickname")
    public ResponseEntity<CheckResponse> checkNickname(@RequestParam String username) {
        boolean available = userService.checkNickname(username);
        return ResponseEntity.ok(new CheckResponse(available));
    }

    @GetMapping("/users/check-email")
    public ResponseEntity<CheckResponse> checkEmail(@RequestParam String email) {
        boolean available = userService.checkEmail(email);
        return ResponseEntity.ok(new CheckResponse(available));
    }

    @PostMapping("/signup/validate-password")
    public ResponseEntity<ValidatePasswordResponse> validatePassword(@RequestBody PasswordRequest request) {
        boolean isValid = userService.validatePassword(request.getPassword());
        String error = isValid ? null : "비밀번호는 8자 이상, 소문자+숫자만 가능합니다.";
        return ResponseEntity.ok(new ValidatePasswordResponse(isValid, error));
    }

    @PostMapping("/users/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(new RegisterResponse(null, "비밀번호가 일치하지 않습니다."));
        }

        User user = userService.registerUser(request.getName(), request.getUsername(),
                request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new RegisterResponse(user.getId().toString(), "회원가입 성공"));
    }
}