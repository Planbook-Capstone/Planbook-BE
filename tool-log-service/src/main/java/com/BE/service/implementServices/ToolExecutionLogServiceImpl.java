package com.BE.service.implementServices;

import com.BE.enums.*;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.feign.IdentityServiceClient;
import com.BE.feign.WebSocketServiceClient;
import com.BE.feign.WorkspaceServiceClient;
import com.BE.mapper.ToolExecutionLogMapper;
import com.BE.model.entity.ToolExecutionLog;
import com.BE.model.request.*;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.ToolExecutionLogResponse;
import com.BE.model.response.ToolResultResponse;
import com.BE.repository.ToolExecutionLogRepository;
import com.BE.service.interfaceServices.IOutboxService;
import com.BE.service.interfaceServices.IToolExecutionLogService;
import com.BE.utils.DateNowUtils;
import com.BE.utils.PageUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ToolExecutionLogServiceImpl implements IToolExecutionLogService {

    final ToolExecutionLogRepository repository;
    final ToolExecutionLogMapper mapper;
    final PageUtil pageUtil;
    final ObjectMapper objectMapper;
    final IOutboxService iOutboxService;
    final WebSocketServiceClient webSocketServiceClient;
    final IdentityServiceClient identityServiceClient;
    final WorkspaceServiceClient workspaceServiceClient;

    @Value("${kafka.topic.name.request}")
    String requestTopic;


    @Override
    @Transactional
    public ToolExecutionLogResponse save(ToolExecutionLogRequest request) {
        ToolExecutionLog log = mapper.toEntity(request);
        log.setStatus(ExecutionStatus.PENDING);
        ToolExecutionLog saved = repository.save(log);
        ToolExecutionLogResponse response = mapper.toResponse(saved);

        if (request.getToolType().equals(ToolTypeEnum.EXTERNAL)) {





        } else {
            try {
                // Serialize log response thành JSON
                Map<String, Object> input = request.getInput();
                ToolKafkaPayload payload = ToolKafkaPayload.builder()
                        .type(request.getCode().toString())
                        .data(KafkaData.builder()
                                .user_id(request.getUserId().toString())
                                .book_id(request.getBookId().toString())
                                .lesson_id(request.getLessonIds().get(0).toString())
                                .tool_log_id(response.getId())
                                .input(input)
                                .timestamp(Instant.now().toString())
                                .build())
                        .build();

                String jsonToSend = objectMapper.writeValueAsString(payload);

                iOutboxService.saveOutbox(requestTopic, jsonToSend, request.getCode().toString(), request.getUserId() + ":" + request.getToolId());

            } catch (JsonProcessingException e) {
                throw new RuntimeException("Lỗi serialize ToolExecutionLog khi ghi outbox", e);
            }
        }


        return response;
    }

//    @Override
//    public Page<ToolExecutionLogResponse> getAll(ToolExecutionLogSearchRequest request) {
//        pageUtil.checkOffset(request.getOffset());
//        Pageable pageable = pageUtil.getPageable(request.getOffset() - 1, request.getPageSize(), request.getSortBy(), request.getSortDirection());
//
//        Specification<ToolExecutionLog> spec = Specification.where(null);
//
//        if (request.getSearch() != null && !request.getSearch().isBlank()) {
//            spec = spec.and((root, query, cb) -> cb.or(
//                    cb.like(cb.lower(root.get("input")), "%" + request.getSearch().toLowerCase() + "%"),
//                    cb.like(cb.lower(root.get("output")), "%" + request.getSearch().toLowerCase() + "%")
//            ));
//        }
//
//        if (request.getToolType() != null) {
//            spec = spec.and((root, query, cb) -> cb.equal(root.get("toolType"), request.getToolType()));
//        }
//
//        if (request.getUserId() != null) {
//            spec = spec.and((root, query, cb) -> cb.equal(root.get("userId"), request.getUserId()));
//        }
//
//        return repository.findAll(spec, pageable).map(mapper::toResponse);
//    }


    @Override
    public Page<ToolExecutionLogResponse> getAll(ToolExecutionLogSearchRequest request) {
        Pageable pageable = pageUtil.getPageable(
                Math.max(request.getOffset(), 1) - 1,
                request.getPageSize(),
                request.getSortBy(),
                request.getSortDirection()
        );

        Specification<ToolExecutionLog> spec = buildSpecification(request);

        return repository.findAll(spec, pageable)
                .map(mapper::toResponse);
    }

    @Override
    public ToolExecutionLogResponse getById(Long id) {
        ToolExecutionLog toolExecutionLog = repository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy tool-log "));
        return mapper.toResponse(toolExecutionLog);
    }

    private Specification<ToolExecutionLog> buildSpecification(ToolExecutionLogSearchRequest request) {
        Specification<ToolExecutionLog> spec = Specification.where(null);

        if (request.getSearch() != null && !request.getSearch().isBlank()) {
            String keyword = "%" + request.getSearch().toLowerCase() + "%";

            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(cb.function("JSON_UNQUOTE", String.class, root.get("input"))), keyword),
                    cb.like(cb.lower(cb.function("JSON_UNQUOTE", String.class, root.get("output"))), keyword)
            ));
        }

        if (request.getToolType() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("toolType"), request.getToolType()));
        }

        if (request.getUserId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("userId"), request.getUserId()));
        }

        return spec;
    }


    @Override
    public void updateOutputByLogId(Long toolLogId, ToolLogUpdateRequest output) {
        ToolExecutionLog log = repository.findById(toolLogId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy log với ID: " + toolLogId));

        if(output.getSuccess()){
            WalletTokenRequest walletTokenRequest = new WalletTokenRequest();
            walletTokenRequest.setAmount(log.getTokenUsed());
            walletTokenRequest.setUserId(log.getUserId());
            identityServiceClient.deduct(walletTokenRequest);
            if (log.getResultId() == null) {
                // 1. Tạo request tạo ToolResult từ dữ liệu của log
                CreateToolResultRequest request = new CreateToolResultRequest();
                request.setUserId(log.getUserId());
                request.setWorkspaceId(log.getWorkspaceId());
                request.setType(convertToToolResultType(log.getCode())); // Mapping Enum nếu cùng tên
                request.setLessonIds(log.getLessonIds());
                request.setName("Auto Result từ ToolLog " + log.getId()); // Có thể tùy biến theo use-case
                request.setDescription("Tự động tạo từ tool execution log");
//                request.setData(output.getOutput());
                request.setStatus(ToolResultStatus.DRAFT);

                try {
                    // 2. Gọi Feign để tạo ToolResult
                    DataResponseDTO<ToolResultResponse> response = workspaceServiceClient.create(request);

                    if (response != null && response.getData() != null) {
                        // 3. Lưu lại resultId vào log
                        Long createdResultId = response.getData().getId();
                        log.setResultId(createdResultId);
                    } else {
                        throw new RuntimeException("Tạo ToolResult thất bại: response không hợp lệ");
                    }

                } catch (Exception e) {
                    throw new RuntimeException("Gọi tạo ToolResult thất bại: " + e.getMessage(), e);
                }
            }


        }
        // Cập nhật output và status
        log.setOutput(output.getOutput());
        log.setStatus(output.getSuccess() ? ExecutionStatus.SUCCESS : ExecutionStatus.FAILED);


        log = repository.save(log);

        if(ToolTypeEnum.INTERNAL.equals(log.getToolType())){

            WebSocketMessageRequest webSocketMessageRequest =  WebSocketMessageRequest.builder()
                    .userId(log.getUserId().toString())
                    .destination("/queue/notifications")
                    .payload(output.getOutput())
                    .build();

            sendWebSocket(webSocketMessageRequest);
        }


    }



        private ToolResultType convertToToolResultType(ToolCodeEnum code) {
            if (code == null) return null;

            return switch (code) {
                case LESSON_PLAN -> ToolResultType.LESSON_PLAN;
                case SLIDE_GENERATOR -> ToolResultType.SLIDE;
                case EXAM_CREATOR -> ToolResultType.EXAM;
                default -> throw new IllegalArgumentException("ToolCodeEnum không map được sang ToolResultType: " + code);
            };
        }


    @Override
    public void sendWebSocket(WebSocketMessageRequest request) {
        webSocketServiceClient.pushToClient(request);
    }

}
