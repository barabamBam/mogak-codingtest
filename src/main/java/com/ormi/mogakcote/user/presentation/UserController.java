package com.ormi.mogakcote.user.presentation;

import static com.ormi.mogakcote.common.CrossOriginConstants.CROSS_ORIGIN_ADDRESS;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.common.model.ResponseDto;
import com.ormi.mogakcote.user.application.UserService;
import com.ormi.mogakcote.user.dto.request.*;

import com.ormi.mogakcote.user.dto.response.ValidatePasswordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = CROSS_ORIGIN_ADDRESS)
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam(name = "username") String username) {
        var response = userService.checkNickname(username);
        return ResponseDto.ok(response);
    }

    @GetMapping("/users/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam(name = "email") String email) {
        var response = userService.existsByEmail(email);
        return ResponseDto.ok(response);
    }

    @PostMapping("/signup/validate-password")
    public ResponseEntity<ValidatePasswordResponse> validatePassword(
            @RequestBody PasswordRequest request) {
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

    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(
            @RequestBody UpdateProfileRequest request,
            AuthUser authUser
    ) {
        userService.updateProfile(
                authUser.getId(),
                request.getUsername(), // getName() 대신 getUsername() 사용
                request.getNickname());
        return ResponseDto.ok("Profile updated successfully");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request,
            AuthUser authUser) {
        userService.changePassword(
                authUser.getId(),
                request.getCurrentPassword(),
                request.getNewPassword()
        );
        return ResponseDto.ok("Password changed successfully");
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(@RequestBody DeleteUserRequest request,
            AuthUser authUser) {
        userService.deleteUser(authUser.getId(), request.getPassword());
        return ResponseDto.ok("User deleted successfully");
    }
}