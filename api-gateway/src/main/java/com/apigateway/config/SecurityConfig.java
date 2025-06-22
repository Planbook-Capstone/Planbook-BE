package com.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity // Bắt buộc cho môi trường reactive của Gateway
public class SecurityConfig {

      // === BƯỚC 1: Inject giá trị từ application.yml ===
//    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
//    private String jwkSetUri;

    private final String[] PUBLIC_ENDPOINTS = {
            "/error",
            "/swagger-ui.html",
            "/v3/api-docs/swagger-config",
            "/webjars/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/*/v3/api-docs/**",
            "/*/api/login"
    };

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Tắt CSRF cho API
                .authorizeExchange(exchange -> exchange
                        // Các đường dẫn (path) được phép truy cập công khai
                        .pathMatchers(PUBLIC_ENDPOINTS).permitAll()
                        // Tất cả các request còn lại phải được xác thực (có JWT hợp lệ)
                        .anyExchange().authenticated()
                )
                // Kích hoạt tính năng xác thực token JWT như một Resource Server
                // === BƯỚC 2: Chỉ định decoder tường minh ===
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())); // Updated line

        return http.build();
    }

      // === BƯỚC 3: Tạo bean ReactiveJwtDecoder ===
//    @Bean
//    public ReactiveJwtDecoder reactiveJwtDecoder() {
//        // Spring sẽ dùng URI này để lấy public key và xác thực token JWT
//        return ReactiveJwtDecoders.fromOidcIssuerLocation(jwkSetUri);
//    }


    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("*"); // Có thể thay bằng domain cụ thể của bạn
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L); // Cache pre-flight request trong 1 giờ
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig); // Áp dụng cho tất cả các đường dẫn
        return new CorsWebFilter(source);
    }
}