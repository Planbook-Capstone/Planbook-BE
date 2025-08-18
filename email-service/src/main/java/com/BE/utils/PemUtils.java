package com.BE.utils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class PemUtils {

    public static PublicKey readPublicKey(String filePathInResources) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePathInResources)) {
            if (is == null) {
                throw new RuntimeException("Không tìm thấy file key: " + filePathInResources);
            }

            String pem = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            String publicKeyPEM = pem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll(System.lineSeparator(), "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .trim();

            byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);

        } catch (Exception e) {
            throw new RuntimeException("Không thể đọc public key từ file PEM", e);
        }
    }
}
