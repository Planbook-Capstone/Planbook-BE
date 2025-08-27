package com.BE.feign;

import com.BE.config.FeignConfig;
import com.BE.model.response.BookTypeResponse;
import com.BE.model.response.DataResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "identity-service",  configuration = FeignConfig.class)
public interface IdentityServiceClient {
    @GetMapping("/api/book-types/{id}")
    DataResponseDTO<BookTypeResponse> getBookTypeById(@PathVariable("id") UUID id);
}
