package com.BE.feign;

import com.BE.config.FeignConfig;
import com.BE.model.request.EmailDataRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "email-service",  configuration = FeignConfig.class)
public interface EmailServiceClient {

    @PostMapping("/api/email/send")
    String sendTemplateEmail(@RequestBody EmailDataRequest request);

}
