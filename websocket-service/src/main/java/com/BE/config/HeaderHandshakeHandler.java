package com.BE.config;

import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;

public class HeaderHandshakeHandler extends DefaultHandshakeHandler {

    private final RSAPublicKey publicKey;

    public HeaderHandshakeHandler(RSAPublicKey publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        try {
            // Đọc token từ query param: ?token=xxx
            String query = request.getURI().getQuery();
            if (query != null && query.startsWith("token=")) {
                String token = query.substring("token=".length());
                SignedJWT jwt = SignedJWT.parse(token);
                if (jwt.verify(new RSASSAVerifier(publicKey))) {
//                    String username = jwt.getJWTClaimsSet().getSubject();
                    String userId = jwt.getJWTClaimsSet().getStringClaim("userId");
                    System.out.println("✅ WebSocket Principal: " + userId);
                    return () -> userId; // Principal đơn giản
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
