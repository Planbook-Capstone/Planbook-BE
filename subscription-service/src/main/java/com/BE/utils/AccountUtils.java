package com.BE.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AccountUtils {


    public String getCurrentUserId(){
        String userId =  SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUsername(userName).orElseThrow();

        return userId;
        }
}
