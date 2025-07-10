package com.BE.utils;

import com.BE.model.entity.AuthUser;
import com.BE.repository.AuthenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AccountUtils {
    @Autowired
    AuthenRepository authenRepository;

    public AuthUser getCurrentUser(){
        String userName=  SecurityContextHolder.getContext().getAuthentication().getName();
        AuthUser auth = authenRepository.findByUsername(userName).orElseThrow();
        return auth;
        }
}
