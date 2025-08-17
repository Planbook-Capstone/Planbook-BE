package com.BE.feign;


import com.BE.model.request.WalletTokenRequest;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.WalletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "identity-service")
public interface IdentityServiceClient {

//    @PostMapping("/auth/login")
//    AuthenticationResponse login(@RequestBody LoginRequestDTO request);

    @PostMapping("/api/wallets/refund")
    DataResponseDTO<WalletResponse> refund(@RequestBody WalletTokenRequest request);


//    @PostMapping("/auth/login-google")
//    AuthenticationResponse loginGoogle(@RequestBody LoginGoogleRequest request);

}
