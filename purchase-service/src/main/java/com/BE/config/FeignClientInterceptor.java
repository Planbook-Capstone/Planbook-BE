package com.BE.config;

import com.BE.filter.CustomUserPrincipal;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserPrincipal principal) {
            template.header("X-User-Id", principal.getUserId());
            template.header("X-Username", principal.getUsername());
            template.header("X-User-Role", principal.getRole());
        }
    }
}

