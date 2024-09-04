package com.ormi.mogakcote.user.application;

import com.ormi.mogakcote.exception.auth.UserAuthManagementInvalidException;
import com.ormi.mogakcote.exception.dto.ErrorType;
import com.ormi.mogakcote.exception.user.UserInvalidException;
import com.ormi.mogakcote.user.domain.User;
import com.ormi.mogakcote.user.dto.request.RegisterRequest;
import com.ormi.mogakcote.user.dto.response.RegisterResponse;
import com.ormi.mogakcote.user.dto.response.UserAuthResponse;
import com.ormi.mogakcote.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.ormi.mogakcote.user.domain.Authority.*;

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
    @Transactional
    public void updateProfile(Long userId, String username, String nickname) {
        User user = getById(userId);
        user.updateProfile(username, nickname);
    }
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = getById(userId);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UserAuthManagementInvalidException(ErrorType.PASSWORD_NOT);
        }
        user.updatePassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public void deleteUser(Long userId, String password) {
        User user = getById(userId);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserAuthManagementInvalidException(ErrorType.PASSWORD_NOT);
        }
        userRepository.delete(user);
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

    public void updateActivity(Long id, String act) {
        User user = getById(id);
        switch(act){
            case "increaseComment":
                user.getActivity().increaseCommentCount();
                break;
            case "decreaseComment":
                user.getActivity().decreaseCommentCount();
                break;
            case "increaseDay":
                user.getActivity().increaseDayCount();
                break;
            case "resetDay":
                user.getActivity().resetDayCount();
        }
        userRepository.save(user);
    }

    public void updateActivity(Long id, String act, LocalDateTime time) {
        User user = getById(id);
		if (act.equals("decreaseDay")) {
			user.getActivity().decreaseDayCount(time);
		}
        userRepository.save(user);
    }


    public UserAuthResponse registerUserAuth(Long id) {
        User findUser = getById(id);
        if (findUser.getAuthority() == BANNED){
            findUser.updateAuth(USER);
        } else if (findUser.getAuthority() == USER) {
            findUser.updateAuth(BANNED);
        } else {
            throw new UserAuthManagementInvalidException(ErrorType.INVALID_AUTH_CHANGE_ERROR);
        }

        return UserAuthResponse.toResponse(
                findUser.getId(),
                findUser.getName(),
                findUser.getNickname(),
                findUser.getEmail(),
                findUser.getPassword(),
                findUser.getAuthority()
        );

    }
}