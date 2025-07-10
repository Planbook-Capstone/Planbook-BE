package com.BE.service;


import com.BE.model.entity.AuthUser;
import com.BE.repository.AuthenRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;

import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JWTService {

    @Value("${spring.duration}")
    private long DURATION;

    @Autowired
    private RSAKey rsaKey;


    @Autowired
    AuthenRepository authenRepository;
    @Autowired
    RefreshTokenService refreshTokenService;


    public String generateToken(AuthUser auth, String refresh, boolean isRefresh) {

        try {

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(auth.getUsername())
                    .issueTime(new Date())
                    .expirationTime(Date.from(Instant.now().plus(Duration.ofSeconds(DURATION))))
                    .claim("scope", "ROLE_" + auth.getRole())
                    .claim("userId", auth.getId())
                    .claim("refresh", refresh)
                    .build();

            if (!isRefresh) {
                refreshTokenService.saveRefreshToken(refresh, auth.getId());
            }
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaKey.getKeyID()).build();
            SignedJWT signedJWT = new SignedJWT(header, claims);
            RSASSASigner signer = new RSASSASigner(rsaKey.toPrivateKey());
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Token signing error", e);
        }

    }


    public String generateToken(AuthUser auth) {
        try {

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(auth.getUsername())
                    .issueTime(new Date())
                    .expirationTime(Date.from(Instant.now().plus(Duration.ofSeconds(DURATION))))
                    .claim("scope", "ROLE_" + auth.getRole())
                    .claim("userId", auth.getId())
                    .build();

            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaKey.getKeyID()).build();
            SignedJWT signedJWT = new SignedJWT(header, claims);
            RSASSASigner signer = new RSASSASigner(rsaKey.toPrivateKey());
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Token signing error", e);
        }
    }

    public AuthUser getUserByToken(String token) {
        try {
            JWTClaimsSet claims = parseAndVerify(token);
            String username = claims.getSubject();
            return authenRepository.findByUsername(username).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Error processing token", e);
        }
    }

    public String getRefreshClaim(String token) {
        try {
            JWTClaimsSet claims = parseAndVerify(token);
            return claims.getStringClaim("refresh");
        } catch (Exception e) {
            throw new RuntimeException("Error processing token", e);
        }
    }

    private JWTClaimsSet parseAndVerify(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        RSASSAVerifier verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());

        if (!signedJWT.verify(verifier)) {
            throw new RuntimeException("Invalid token signature");
        }

        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

        // Optional: check expiration manually (nếu muốn custom error)
        Date now = new Date();
        if (claims.getExpirationTime() != null && claims.getExpirationTime().before(now)) {
            throw new RuntimeException("Token expired");
        }

        return claims;
    }


}
