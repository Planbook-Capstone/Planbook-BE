package com.BE.feign;

import com.BE.model.request.ToolExecuteExternalRequest;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.ExternalToolConfigResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "external-caller-service")
public interface ToolExternalCallerServiceClient {

    @PostMapping("/api/partner-tools")
    DataResponseDTO<Map<String, Object>> executeExternalTool(@RequestBody ToolExecuteExternalRequest toolExecuteExternalRequest);

}
