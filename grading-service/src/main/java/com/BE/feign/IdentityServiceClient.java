package com.BE.feign;

import com.BE.config.FeignConfig;
import com.BE.model.request.WalletTokenRequest;
import com.BE.model.response.BookTypeResponse;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.WalletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "identity-service",  configuration = FeignConfig.class)
public interface IdentityServiceClient {

    @GetMapping("/api/book-types/{id}")
    DataResponseDTO<BookTypeResponse> getBookTypeById(@PathVariable("id") UUID id);

    @PostMapping("/api/wallets/deduct")
    DataResponseDTO<WalletResponse> deduct(@RequestBody WalletTokenRequest request);
}
