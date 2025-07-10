package com.BE.mapper;

import com.BE.model.entity.ToolExecutionLog;
import com.BE.model.request.ToolExecutionLogRequest;
import com.BE.model.response.ToolExecutionLogResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ToolExecutionLogMapper {
    ToolExecutionLog toEntity(ToolExecutionLogRequest request);
    ToolExecutionLogResponse toResponse(ToolExecutionLog entity);

}
