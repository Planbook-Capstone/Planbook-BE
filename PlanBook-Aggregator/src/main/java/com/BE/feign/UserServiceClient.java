package com.BE.feign;

import com.BE.model.request.UserProfileRequest;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/user-profile/{id}")
    DataResponseDTO<UserProfileResponse> getProfile(@PathVariable("id") UUID id);

    @PostMapping("/api/user-profile/{id}")
    DataResponseDTO<UserProfileResponse> createProfile(@PathVariable("id") UUID id, @RequestBody UserProfileRequest request);

}
