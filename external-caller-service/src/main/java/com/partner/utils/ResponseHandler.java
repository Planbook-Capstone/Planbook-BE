package com.partner.utils;



import com.partner.model.response.DataResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class ResponseHandler<T> {

    public ResponseEntity<DataResponseDTO<T>> response(int statusCode, String message, T data) {
        HttpStatus httpStatus = HttpStatus.resolve(statusCode);
        if (httpStatus == null) {
            httpStatus = HttpStatus.OK; // fallback
        }

        return ResponseEntity.status(httpStatus).body(
                DataResponseDTO.<T>builder()
                        .statusCode(statusCode)
                        .message(message)
                        .data(data)
                        .build());

    }

}