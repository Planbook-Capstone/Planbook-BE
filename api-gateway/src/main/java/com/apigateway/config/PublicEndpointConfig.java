package com.apigateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Configuration
public class PublicEndpointConfig {

    private final List<String> publicEndpoints = Arrays.asList(
            "/error",
            "/swagger-ui.html",
            "/v3/api-docs/swagger-config",
            "/webjars/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/*/v3/api-docs/**",
            "/*/api/login",
            "/*/api/register",
            "/*/api/login-google",
            "/*/api/forgot-password",
            "/*/api/status",
            "/*/api/refresh",
            "/*/api/logout",
            "/*/api/grades",
            "/*/api/grades/**",
            "/*/api/subjects",
            "/*/api/subjects/**",
            "/*/api/books",
            "/*/api/books/**",
            "/*/api/chapters",
            "/*/api/chapters/**",
            "/*/api/lessons",
            "/*/api/lessons/**",
            "/*/api/book-types",
            "/*/api/book-types/**",
            "/*/api/sendMessage"
    );

    private final List<String> publicGetEndpoints = Arrays.asList(
            "/*/api/testRole"
    );

    public List<String> getPublicEndpoints() {
        return publicEndpoints;
    }

    public List<String> getPublicGetEndpoints() {
        return publicGetEndpoints;
    }
}
