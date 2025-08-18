package com.BE.config;

import com.BE.exception.exceptions.AuthenException;
import com.BE.exception.exceptions.NotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String errorMessage = "Unknown error from downstream service";
        try {
            if (response.body() != null) {
                errorMessage = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException ignored) {}

        HttpStatus status = HttpStatus.valueOf(response.status());
        log.error("Feign call error - status: {}, method: {}, body: {}", status, methodKey, errorMessage);

        return switch (status) {
            case BAD_REQUEST -> new IllegalArgumentException(errorMessage);
            case NOT_FOUND -> new NotFoundException(errorMessage);
            case UNAUTHORIZED, FORBIDDEN -> new AuthenException("Access denied: " + errorMessage);
            case INTERNAL_SERVER_ERROR, CONFLICT -> new RuntimeException("Internal server error: " + errorMessage);
            default -> new RuntimeException("Unhandled error: " + errorMessage);
        };
    }
}
