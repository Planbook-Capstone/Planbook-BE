package com.BE.feign;

import com.BE.model.request.ToolExecutionLogRequest;
import com.BE.model.request.ToolLogUpdateRequest;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.ToolExecutionLogResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "tool-log-service")
public interface ToolLogServiceClient {

    @PostMapping("/api/tool-logs")
    DataResponseDTO<ToolExecutionLogResponse> toolExecutionLog(ToolExecutionLogRequest toolExecutionLogRequest);

    @PostMapping("/api/tool-logs/{Id}/output")
    DataResponseDTO updateOutput(Long id, ToolLogUpdateRequest request);



}
