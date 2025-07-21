package com.BE.config;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

@Configuration
@ConfigurationProperties(prefix = "payos")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PayosConfig {
    String clientId;
    String apiKey;
    String checksumKey;
    @Bean
    public PayOS payOS() {
        return new PayOS(clientId, apiKey, checksumKey);
    }
}

