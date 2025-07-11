package com.BE.utils;

import com.BE.model.entity.UserProfile;
import com.BE.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AccountUtils {
    @Autowired
    UserProfileRepository userRepository;

    public UserProfile getCurrentUser(){
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserProfile user = userRepository.findById(userId).orElseThrow();
        return user;
        }
}
