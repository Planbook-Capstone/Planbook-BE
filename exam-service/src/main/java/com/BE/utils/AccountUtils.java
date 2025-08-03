package com.BE.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AccountUtils {

    /**
     * Get current user ID from security context
     * @return UUID of current user
     */
    public UUID getCurrentUserId() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return UUID.fromString(userId);
    }

    /**
     * Get current user role from security context
     * @return String role of current user
     * @throws RuntimeException if user is not authenticated or role not found
     */
    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }

        return authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new RuntimeException("User role not found"));
    }

    /**
     * Check if current user is staff
     * @return true if current user has STAFF role
     */
    public boolean isCurrentUserStaff() {
        try {
            String role = getCurrentUserRole();
            System.out.println(role);
            return "ROLE_STAFF".equalsIgnoreCase(role);
        } catch (RuntimeException e) {
            return false;
        }
    }
}
