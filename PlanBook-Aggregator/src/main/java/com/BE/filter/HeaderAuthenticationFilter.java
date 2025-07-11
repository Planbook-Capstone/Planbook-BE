package com.BE.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String username = request.getHeader("X-Username");
        String role = request.getHeader("X-User-Role");
        String userId = request.getHeader("X-User-Id");
        if (username != null && !username.isBlank() &&  userId != null && !userId.isBlank() && role != null && !role.isBlank()) {
            var auth = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority(role))
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
