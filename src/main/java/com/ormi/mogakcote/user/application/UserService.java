package com.ormi.mogakcote.user.application;

import com.ormi.mogakcote.exception.dto.ErrorType;
import com.ormi.mogakcote.exception.user.UserInvalidException;
import com.ormi.mogakcote.user.domain.Authority;
import com.ormi.mogakcote.user.domain.User;
import com.ormi.mogakcote.user.dto.request.RegisterRequest;
import com.ormi.mogakcote.user.dto.response.RegisterResponse;
import com.ormi.mogakcote.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterResponse registerUser(RegisterRequest request) {
        validatePassword(request.getPassword());


        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new UserInvalidException(ErrorType.PASSWORD_NOT);
        }

        User savedUser = buildAndSaveUser(request);

        return RegisterResponse.toResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getNickname(),
                savedUser.getEmail(),
                savedUser.getPassword()
        );
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR));
    }

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR));
    }

    public boolean checkNickname(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    @Transactional(readOnly = true)
    public String getEmailByNameAndNickname(String name, String nickname) {
        return userRepository.findEmailByNameAndNickname(name, nickname).orElseThrow(() -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR));
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean validatePassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,}$");
    }

    @Transactional
    public void updatePassword(String email, String newPassword) {
        int result = userRepository.updatePasswordByEmail(email, newPassword);
        if (result <= 0) {
            throw new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR);
        }
    }

    private User buildAndSaveUser(RegisterRequest request) {
        User user = User.builder()
                .name(request.getUsername())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .authority(request.getAuthority())
                .joinAt(LocalDateTime.now())
                .build();
        return userRepository.save(user);
    }


}