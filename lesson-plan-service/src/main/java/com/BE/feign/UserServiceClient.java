package com.BE.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service-local")
public interface UserServiceClient {

    @GetMapping("/api/sendMessage")
    String sendMessageToKafka(@RequestParam("message") String message);

}
