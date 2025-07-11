package com.BE.feign;

import com.BE.model.request.AuthenticationRequest;
import com.BE.model.response.AuthenticationResponse;
import com.BE.model.response.DataResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {

//    @PostMapping("/auth/login")
//    AuthenticationResponse login(@RequestBody LoginRequestDTO request);

    @PostMapping("/api/register")
    DataResponseDTO<AuthenticationResponse> register(@RequestBody AuthenticationRequest request);

//    @PostMapping("/auth/login-google")
//    AuthenticationResponse loginGoogle(@RequestBody LoginGoogleRequest request);

}
