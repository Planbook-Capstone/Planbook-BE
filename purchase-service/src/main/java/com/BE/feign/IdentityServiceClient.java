package com.BE.feign;

import com.BE.config.FeignConfig;
import com.BE.model.request.WalletTransactionRequest;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.UserResponse;
import com.BE.model.response.WalletTransactionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "identity-service",  configuration = FeignConfig.class)
public interface IdentityServiceClient {

        @PostMapping("/api/wallets/recharge")
        DataResponseDTO<WalletTransactionResponse> recharge(@RequestBody WalletTransactionRequest request);

        @GetMapping("/api/users/{id}")
        DataResponseDTO<UserResponse> getUserById(@PathVariable("id") UUID id);

}
