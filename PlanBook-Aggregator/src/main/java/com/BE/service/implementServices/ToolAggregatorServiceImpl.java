package com.BE.service.implementServices;

import com.BE.enums.ToolCodeEnum;
import com.BE.enums.ToolStatusEnum;
import com.BE.enums.ToolTypeEnum;
import com.BE.exception.exceptions.WalletTokenException;
import com.BE.feign.IdentityServiceClient;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ToolAggregatorServiceImpl implements IToolAggregatorService {

    ToolExternalServiceClient toolExternalServiceClient;
    ToolExternalCallerServiceClient toolExternalCallerServiceClient;
    IdentityServiceClient toolInternalServiceClient;
    ToolLogServiceClient toolLogServiceClient;
    ToolAggregatorMapper toolAggregatorMapper;
    AccountUtils accountUtils;

    private void checkToken(Integer tokenCostPerQuery){
        WalletTokenRequest request = new WalletTokenRequest();
        request.setAmount(tokenCostPerQuery);
        request.setUserId(accountUtils.getCurrentUserId());

        if(!toolInternalServiceClient.checkSufficientToken(request)){
            throw new WalletTokenException("Không đủ token trong ví để thực hiện hành động");
        }
    }


    @Override
    public String executeInternalTool(ToolExecuteRequest request) {

        DataResponseDTO<BookTypeResponse> internalToolConfigResponse = toolInternalServiceClient.getBookTypeById(request.getToolId());
        checkToken(internalToolConfigResponse.getData().getTokenCostPerQuery());

        List<Long> lessons = new ArrayList<>();

        if (ToolCodeEnum.EXAM_CREATOR.equals(internalToolConfigResponse.getData().getCode())) {
            Object matrixObj = request.getInput().get("matrix");
            if (matrixObj instanceof List<?> matrixList) {
                for (Object item : matrixList) {
                    if (item instanceof Map<?, ?> lessonEntry) {
                        Object lessonIdRaw = lessonEntry.get("lessonId");
                        if (lessonIdRaw != null) {
                            try {
                                Long lessonId = Long.valueOf(lessonIdRaw.toString());
                                lessons.add(lessonId);
                            } catch (NumberFormatException e) {
                                throw new IllegalArgumentException("lessonId không hợp lệ trong matrix: " + lessonIdRaw);
                            }
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException("Trường 'matrix' trong input phải là một danh sách");
            }
        } else {
            lessons.add(request.getLesson_id());
        }


        ToolExecutionLogRequest toolExecutionLogRequest = ToolExecutionLogRequest.builder()
                .userId(accountUtils.getCurrentUserId())
                .toolId(request.getToolId())
                .toolType(request.getToolType())
                .code(internalToolConfigResponse.getData().getCode())
                .input(request.getInput())
                .bookId(request.getBook_id())
                .lessonIds(lessons)
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
        checkToken(externalToolConfigResponse.getData().getTokenCostPerQuery());
        List<Long> lessons = new ArrayList<>();
        lessons.add(request.getLesson_id());
        ToolExecutionLogRequest toolExecutionLogRequest = ToolExecutionLogRequest.builder()
                .userId(accountUtils.getCurrentUserId())
                .toolId(request.getToolId())
                .toolType(request.getToolType())
//                .code(externalToolConfigResponse.getData().getCode())
                .code(ToolCodeEnum.QUIZ_GAME)
                .input(request.getInput())
                .lessonIds(lessons)
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
