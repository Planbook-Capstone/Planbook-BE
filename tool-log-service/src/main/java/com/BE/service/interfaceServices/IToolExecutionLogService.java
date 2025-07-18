package com.BE.service.interfaceServices;

import com.BE.model.request.ToolExecutionLogRequest;
import com.BE.model.request.ToolExecutionLogSearchRequest;
import com.BE.model.request.ToolLogUpdateRequest;
import com.BE.model.request.WebSocketMessageRequest;
import com.BE.model.response.ToolExecutionLogResponse;
import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.UUID;

public interface IToolExecutionLogService {
    ToolExecutionLogResponse save(ToolExecutionLogRequest request);
    Page<ToolExecutionLogResponse> getAll(ToolExecutionLogSearchRequest request);

    ToolExecutionLogResponse getById(Long id);

    void updateOutputByLogId(Long toolLogId, ToolLogUpdateRequest output);

    void sendWebSocket(WebSocketMessageRequest request);

}
