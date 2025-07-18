package com.BE.feign;

import com.BE.model.request.AuthenticationRequest;
import com.BE.model.response.AuthenticationResponse;
import com.BE.model.response.BookTypeResponse;
import com.BE.model.response.DataResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {

//    @PostMapping("/auth/login")
//    AuthenticationResponse login(@RequestBody LoginRequestDTO request);

    @PostMapping("/api/register")
    DataResponseDTO<AuthenticationResponse> register(@RequestBody AuthenticationRequest request);

    @GetMapping("/api/book-types/{id}")
    DataResponseDTO<BookTypeResponse> getBookTypeById(@PathVariable("id") UUID id);


    @GetMapping("/api/book-types")
    DataResponseDTO<Page<BookTypeResponse>> getBookTypes(
            @RequestParam Map<String, Object> params
    );

//    @PostMapping("/auth/login-google")
//    AuthenticationResponse loginGoogle(@RequestBody LoginGoogleRequest request);

}
