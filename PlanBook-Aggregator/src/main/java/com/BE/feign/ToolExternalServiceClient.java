package com.BE.feign;

import com.BE.config.FeignConfig;
import com.BE.enums.ToolStatusEnum;
import com.BE.model.request.ToolSearchPageRequest;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.ExternalToolConfigResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "external-tool-config-service",  configuration = FeignConfig.class)
public interface ToolExternalServiceClient {

    @GetMapping("/api/external-tools/{id}")
    DataResponseDTO<ExternalToolConfigResponse> getById(@PathVariable("id") UUID id);

    @GetMapping("/api/external-tools")
    DataResponseDTO<Page<ExternalToolConfigResponse>> getAll(
            @RequestParam Map<String, Object> params
    );
}
