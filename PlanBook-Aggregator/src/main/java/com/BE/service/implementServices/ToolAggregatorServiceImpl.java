package com.BE.service.implementServices;

import com.BE.enums.ToolStatusEnum;
import com.BE.enums.ToolTypeEnum;
import com.BE.feign.AuthServiceClient;
import com.BE.feign.ToolExternalCallerServiceClient;
import com.BE.feign.ToolExternalServiceClient;
import com.BE.feign.ToolLogServiceClient;
import com.BE.mapper.ToolAggregatorMapper;
import com.BE.model.request.*;
import com.BE.model.response.*;
import com.BE.service.interfaceServices.IToolAggregatorService;
import com.BE.utils.AccountUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ToolAggregatorServiceImpl implements IToolAggregatorService {

    ToolExternalServiceClient toolExternalServiceClient;
    ToolExternalCallerServiceClient toolExternalCallerServiceClient;
    AuthServiceClient toolInternalServiceClient;
    ToolLogServiceClient toolLogServiceClient;

    ToolAggregatorMapper toolAggregatorMapper;

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
    public AggregatedToolResponse getAggregatedToolInfo(ToolSearchPageRequest request) {
        Map<String, Object> externalToolParams = new HashMap<>();
        Map<String, Object> bookTypeParams = new HashMap<>();

        ToolStatusEnum status = request.getStatus();
        ToolTypeEnum toolType = request.getToolType();


        if (request.getCreatedBy() != null) externalToolParams.put("createdBy", request.getCreatedBy());
        if (request.getOffset() != null) {
            externalToolParams.put("offset", request.getOffset());
            bookTypeParams.put("page", request.getOffset());
        }
        if (request.getPageSize() != null) {
            externalToolParams.put("pageSize", request.getPageSize());
            bookTypeParams.put("size", request.getPageSize());
        }
        if (request.getSearch() != null && !request.getSearch().isBlank()) {
            externalToolParams.put("search", request.getSearch());
            bookTypeParams.put("search", request.getSearch());
        }
        if (request.getSortBy() != null) {
            externalToolParams.put("sortBy", request.getSortBy().name());
            bookTypeParams.put("sortBy", request.getSortBy().name());
        }
        if (request.getSortDirection() != null) {
            externalToolParams.put("sortDirection", request.getSortDirection().name());
            bookTypeParams.put("sortDirection", request.getSortDirection().name());
        }
        if (status != null) {
            externalToolParams.put("status", status.name());
            if (status == ToolStatusEnum.ACTIVE || status == ToolStatusEnum.INACTIVE) {
                bookTypeParams.put("status", status.name());
            }
        }

        DataResponseDTO<Page<ExternalToolConfigResponse>> externalTools = new DataResponseDTO<>();
        if (toolType == null || toolType == ToolTypeEnum.EXTERNAL) {
            externalTools = toolExternalServiceClient.getAll(externalToolParams);
        }

        DataResponseDTO<Page<BookTypeResponse>> internalToool = new DataResponseDTO<>();
        if (toolType == null || toolType == ToolTypeEnum.INTERNAL) {
            internalToool = toolInternalServiceClient.getBookTypes(bookTypeParams);
        }


        Page<ExternalToolConfigPublicResponse> externalToolPage = externalTools.getData()
                .map(toolAggregatorMapper::toPublicResponse);

        return AggregatedToolResponse.builder()
                .externalTools(externalToolPage)
                .internalTools(internalToool.getData())
                .build();
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
