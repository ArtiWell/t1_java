package ru.t1.java.demo.config.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserCredentialsImpl implements UserCredentials {
    @Override
    public String getLogin() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
