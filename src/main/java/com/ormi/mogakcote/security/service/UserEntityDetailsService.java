package com.ormi.mogakcote.security.service;

import com.ormi.mogakcote.security.model.UserEntityDetails;
import com.ormi.mogakcote.exception.user.UserInvalidException;
import com.ormi.mogakcote.user.application.UserService;
import com.ormi.mogakcote.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserEntityDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public UserEntityDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userService.getByEmail(username);
            return new UserEntityDetails(user);
        } catch (UserInvalidException e) {
            throw new UsernameNotFoundException("Email " + username + "not found", e);
        }
    }
}
