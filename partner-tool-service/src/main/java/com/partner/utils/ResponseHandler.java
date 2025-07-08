package com.partner.utils;



import com.partner.model.response.DataResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;



@Component
public class ResponseHandler<T> {

    public ResponseEntity<DataResponseDTO<T>> response(int statusCode, String message, T data) {
        return ResponseEntity.ok(new DataResponseDTO<>(statusCode, message, data));
    }
}
