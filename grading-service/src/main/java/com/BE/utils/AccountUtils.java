package com.BE.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AccountUtils {


    public UUID getCurrentUserId(){
        String userId =  SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return UUID.fromString(userId);
    }
}
