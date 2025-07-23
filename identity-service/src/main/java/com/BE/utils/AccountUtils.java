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
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User user = authenRepository.findById(UUID.fromString(userId)).orElseThrow();
        return user;
    }
}
