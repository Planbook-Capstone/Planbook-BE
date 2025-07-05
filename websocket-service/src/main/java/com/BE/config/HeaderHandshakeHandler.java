package com.BE.config;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
            String token = extractJwtFromHeader(request);
            SignedJWT jwt = SignedJWT.parse(token);
            JWSVerifier verifier = new RSASSAVerifier(publicKey);

            if (jwt.verify(verifier)) {
                var claims = jwt.getJWTClaimsSet();
                String username = claims.getSubject();
                String role = (String) claims.getClaim("scope");
                return new UsernamePasswordAuthenticationToken(username, null,
                        List.of(new SimpleGrantedAuthority(role)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String extractJwtFromHeader(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
