package com.apigateway.config;

import com.apigateway.filter.JwtBypassWebFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity // Bắt buộc cho môi trường reactive của Gateway
public class SecurityConfig {

    @Autowired
    private PublicEndpointConfig publicEndpointConfig;

    @Autowired
    private JwtBypassWebFilter jwtBypassWebFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .addFilterAt(jwtBypassWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Tắt CSRF cho API
                .authorizeExchange(exchange -> exchange
                        // Các đường dẫn (path) được phép truy cập công khai
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(publicEndpointConfig.getPublicEndpoints().toArray(new String[0])).permitAll()
                        .pathMatchers(HttpMethod.GET, publicEndpointConfig.getPublicGetEndpoints().toArray(new String[0])).permitAll()
                        // Tất cả các request còn lại phải được xác thực (có JWT hợp lệ)
                        .anyExchange().authenticated()
                )
                // Kích hoạt tính năng xác thực token JWT như một Resource Server
                // === BƯỚC 2: Chỉ định decoder tường minh ===
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())); // Updated line

        return http.build();
    }


    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("*");  // Hoặc domain cụ thể
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(false);
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
}