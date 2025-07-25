package com.BE.utils;

import com.BE.model.entity.User;
import com.BE.repository.AuthenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AccountUtils {
    @Autowired
    AuthenRepository authenRepository;

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = authenRepository.findByUsername(username).orElseThrow();
        return user;
    }
}
