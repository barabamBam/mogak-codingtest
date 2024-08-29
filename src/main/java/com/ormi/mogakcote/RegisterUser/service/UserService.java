package com.ormi.mogakcote.RegisterUser.service;

import com.ormi.mogakcote.RegisterUser.entity.User;
import com.ormi.mogakcote.RegisterUser.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean checkNickname(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    public boolean checkEmail(String email) {
        return !userRepository.existsByEmail(email);
    }

    public boolean validatePassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,}$");
    }

    @Transactional
    public User registerUser(String name, String nickname, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setAuthority(User.Authority.USER);
        user.setJoinedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}