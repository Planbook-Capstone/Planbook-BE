package com.BE.service.interfaceServices;

import com.BE.model.request.ToolExecutionLogRequest;
import com.BE.model.request.ToolExecutionLogSearchRequest;
import com.BE.model.response.ToolExecutionLogResponse;
import org.springframework.data.domain.Page;

public interface IToolExecutionLogService {
    ToolExecutionLogResponse save(ToolExecutionLogRequest request);
    Page<ToolExecutionLogResponse> getAll(ToolExecutionLogSearchRequest request);
}
