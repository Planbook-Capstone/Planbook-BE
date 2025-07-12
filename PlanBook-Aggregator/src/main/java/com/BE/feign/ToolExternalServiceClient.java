package com.BE.feign;

import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.ExternalToolConfigResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "external-tool-config-service")
public interface ToolExternalServiceClient {

    @GetMapping("/{id}")
    DataResponseDTO<ExternalToolConfigResponse> getById(@PathVariable("id") UUID id);
}
