package com.BE.config;
import com.BE.utils.PemUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic/", "/queue/");
        registry.setUserDestinationPrefix("/user"); // Cần để gửi tới user cụ thể
        registry.setApplicationDestinationPrefixes("/app");
    }

    private RSAPublicKey loadPublicKey() {
        try {
            return (RSAPublicKey) PemUtils.readPublicKey("keys/public.pem");
        } catch (Exception e) {
            throw new RuntimeException("Không thể load public key", e);
        }
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket")
                .setHandshakeHandler(new HeaderHandshakeHandler(loadPublicKey()))
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}