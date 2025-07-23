package com.BE.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class JwkSetController {

    private final RSAKey rsaKey;

    @Autowired
    public JwkSetController(RSAKey rsaKey) {
        this.rsaKey = rsaKey;
    }

    @GetMapping("api/oauth2/jwks")
    public Map<String, Object> keys() {
        return new JWKSet(rsaKey.toPublicJWK()).toJSONObject();
    }
}
