package com.BE.service.implementServices;

import com.BE.feign.AuthServiceClient;
import com.BE.feign.ToolExternalServiceClient;
import com.BE.feign.ToolLogServiceClient;
import com.BE.model.request.ToolExecuteRequest;
import com.BE.model.request.ToolExecutionLogRequest;
import com.BE.model.response.BookTypeResponse;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.ExternalToolConfigResponse;
import com.BE.model.response.ToolExecutionLogResponse;
import com.BE.service.interfaceServices.IToolAggregatorService;
import com.BE.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ToolAggregatorServiceImpl implements IToolAggregatorService {

    ToolExternalServiceClient toolExternalServiceClient;
//    ToolInternalServiceClient toolInternalServiceClient;
    AuthServiceClient toolInternalServiceClient;

    ToolLogServiceClient toolLogServiceClient;
    AccountUtils accountUtils;


    @Override
    public String executeInternalTool(ToolExecuteRequest request) {
        DataResponseDTO<BookTypeResponse> internalToolConfigResponse = toolInternalServiceClient.getBookTypeById(request.getToolId());

        ToolExecutionLogRequest toolExecutionLogRequest = ToolExecutionLogRequest.builder()
                .userId(accountUtils.getCurrentUserId())
                .toolId(request.getToolId())
                .toolType(request.getToolType())
                .toolName(internalToolConfigResponse.getData().getName())
                .inputJson(request.getLesson_plan_json())
                .lessonId(request.getLesson_id())
                .build();
        DataResponseDTO<ToolExecutionLogResponse> response = toolLogServiceClient.toolExecutionLog(toolExecutionLogRequest);

        return response.getMessage();
    }

    @Override
    public String  executeExternalTool(ToolExecuteRequest request) {

        DataResponseDTO<ExternalToolConfigResponse> externalToolConfigResponse = toolExternalServiceClient.getById(request.getToolId());
        ToolExecutionLogRequest toolExecutionLogRequest = ToolExecutionLogRequest.builder()
                .userId(accountUtils.getCurrentUserId())
                .toolId(request.getToolId())
                .toolType(request.getToolType())
                .toolName(externalToolConfigResponse.getData().getName())
                .inputJson(request.getLesson_plan_json())
                .lessonId(request.getLesson_id())
                .build();

        DataResponseDTO<ToolExecutionLogResponse> response = toolLogServiceClient.toolExecutionLog(toolExecutionLogRequest);
        return response.getMessage();
    }


//    public long getLessonById(long id){
//        masterDataServiceClient.getLessonById(id);
//
//        return null;
//    }
}
