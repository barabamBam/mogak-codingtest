package com.ormi.mogakcote.security.model;

import com.ormi.mogakcote.user.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UserEntityDetails implements UserDetails {

    @Getter
    private final User user;
    private final Set<GrantedAuthority> authorities = new HashSet<>();

    public UserEntityDetails(User user) {
        assert user != null;
        this.user = user;
        addRole(user.getAuthority().toString());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void addRole(String role) {
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
    }
}
