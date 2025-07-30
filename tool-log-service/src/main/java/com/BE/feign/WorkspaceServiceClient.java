package com.BE.feign;

import com.BE.model.request.CreateToolResultRequest;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.ToolResultResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "workspace-service")
public interface WorkspaceServiceClient {

    @PostMapping("/api/tool-results")
    DataResponseDTO<ToolResultResponse> create(CreateToolResultRequest request);

}
