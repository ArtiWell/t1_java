package ru.t1.java.demo.config.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.t1.java.demo.entity.UserEntity;

import java.util.Collection;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final String username;
    private final String password;

    public UserDetailsImpl(UserEntity entity) {
        this.username = entity.getLogin();
        this.password = entity.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
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
}
