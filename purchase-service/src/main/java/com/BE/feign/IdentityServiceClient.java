package com.BE.feign;

import com.BE.model.request.WalletTransactionRequest;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.WalletTransactionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "identity-service")
public interface IdentityServiceClient {

        @PostMapping("/api/wallets/recharge")
        DataResponseDTO<WalletTransactionResponse> recharge(@RequestBody WalletTransactionRequest request);
}
