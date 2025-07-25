package com.BE.service.implementServices;

import com.BE.enums.ExecutionStatus;
import com.BE.enums.ToolTypeEnum;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.feign.WebSocketServiceClient;
import com.BE.mapper.ToolExecutionLogMapper;
import com.BE.model.entity.ToolExecutionLog;
import com.BE.model.request.*;
import com.BE.model.response.ToolExecutionLogResponse;
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
    final DateNowUtils dateNowUtils;
    final PageUtil pageUtil;
    final ObjectMapper objectMapper;
    final IOutboxService iOutboxService;
    final WebSocketServiceClient webSocketServiceClient;

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
                        .type(request.getToolName())
                        .data(KafkaData.builder()
                                .user_id(request.getUserId().toString())
                                .lesson_id(request.getLessonId().toString())
                                .tool_log_id(response.getId())
                                .input(input)
                                .timestamp(Instant.now().toString())
                                .build())
                        .build();

                String jsonToSend = objectMapper.writeValueAsString(payload);

                iOutboxService.saveOutbox(requestTopic, jsonToSend, response.getToolName(), request.getUserId() + ":" + request.getToolId());

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

    @Override
    public void sendWebSocket(WebSocketMessageRequest request) {
        webSocketServiceClient.pushToClient(request);
    }

}
