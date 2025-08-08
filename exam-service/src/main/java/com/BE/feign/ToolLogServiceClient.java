package com.BE.feign;

import com.BE.config.FeignConfig;
import com.BE.model.request.ToolExecutionLogRequest;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.ToolExecutionLogResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
@FeignClient(name = "tool-log-service",  configuration = FeignConfig.class)
public interface ToolLogServiceClient {

    @PostMapping("/api/tool-logs")
    DataResponseDTO<ToolExecutionLogResponse> toolExecutionLog(ToolExecutionLogRequest toolExecutionLogRequest);




}
