package com.BE.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AccountUtils {


    public UUID getCurrentUserId(){
        String userId =  SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
//        User user = userRepository.findByUsername(userName).orElseThrow();

        return UUID.fromString(userId);
        }

    public List<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return List.of(); // Trả về danh sách rỗng nếu chưa login hoặc không có quyền
        }

        // Lấy danh sách role (authority) dưới dạng String
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

}
