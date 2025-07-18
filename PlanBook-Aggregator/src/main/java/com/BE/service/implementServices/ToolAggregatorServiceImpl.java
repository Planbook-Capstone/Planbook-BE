package com.BE.service.implementServices;

import com.BE.feign.AuthServiceClient;
import com.BE.feign.ToolExternalCallerServiceClient;
import com.BE.feign.ToolExternalServiceClient;
import com.BE.feign.ToolLogServiceClient;
import com.BE.model.request.ToolExecuteExternalRequest;
import com.BE.model.request.ToolExecuteRequest;
import com.BE.model.request.ToolExecutionLogRequest;
import com.BE.model.request.ToolLogUpdateRequest;
import com.BE.model.response.BookTypeResponse;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.ExternalToolConfigResponse;
import com.BE.model.response.ToolExecutionLogResponse;
import com.BE.service.interfaceServices.IToolAggregatorService;
import com.BE.utils.AccountUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ToolAggregatorServiceImpl implements IToolAggregatorService {

    ToolExternalServiceClient toolExternalServiceClient;
    ToolExternalCallerServiceClient toolExternalCallerServiceClient;
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
                .input(request.getInput())
                .lessonId(request.getLesson_id())
                .tokenUsed(internalToolConfigResponse.getData().getTokenCostPerQuery())
                .build();
        DataResponseDTO<ToolExecutionLogResponse> response = toolLogServiceClient.toolExecutionLog(toolExecutionLogRequest);

        return response.getMessage();
    }

    @Override
    public Map<String, Object> executeExternalTool(ToolExecuteRequest request) {
        DataResponseDTO<ExternalToolConfigResponse> externalToolConfigResponse = toolExternalServiceClient.getById(request.getToolId());
        ToolExecutionLogRequest toolExecutionLogRequest = ToolExecutionLogRequest.builder()
                .userId(accountUtils.getCurrentUserId())
                .toolId(request.getToolId())
                .toolType(request.getToolType())
                .toolName(externalToolConfigResponse.getData().getName())
                .input(request.getInput())
                .lessonId(request.getLesson_id())
                .tokenUsed(externalToolConfigResponse.getData().getTokenCostPerQuery())
                .build();

        DataResponseDTO<ToolExecutionLogResponse> response = toolLogServiceClient.toolExecutionLog(toolExecutionLogRequest);

        ToolExecuteExternalRequest toolExecuteExternalRequest = ToolExecuteExternalRequest.builder()
                .toolName(externalToolConfigResponse.getData().getName())
                .apiUrl(externalToolConfigResponse.getData().getApiUrl())
                .clientId(externalToolConfigResponse.getData().getClientId())
                .clientSecret(externalToolConfigResponse.getData().getClientSecret())
                .tokenUrl(externalToolConfigResponse.getData().getTokenUrl())
                .payload(request.getInput())
                .build();

        DataResponseDTO<Map<String, Object>> outputExternalResponse = toolExternalCallerServiceClient.executeExternalTool(toolExecuteExternalRequest);

        boolean success = Boolean.TRUE.equals(outputExternalResponse.getData().get("success"));
        Object data = outputExternalResponse.getData().get("data");
        String message = (String) outputExternalResponse.getData().getOrDefault("message", "Không rõ lỗi");

        if (!success) {
            toolLogServiceClient.updateOutput(response.getData().getId(),
                    ToolLogUpdateRequest.builder()
                            .success(false)
                            .output(Map.of("error", message))
                            .build()
            );
            throw new RuntimeException("External tool thất bại: " + message);
        }

        toolLogServiceClient.updateOutput(response.getData().getId(),
                ToolLogUpdateRequest.builder()
                        .success(true)
                        .output((Map<String, Object>) data)
                        .build()
        );


        return outputExternalResponse.getData();
    }


//    public long getLessonById(long id){
//        masterDataServiceClient.getLessonById(id);
//
//        return null;
//    }
}
