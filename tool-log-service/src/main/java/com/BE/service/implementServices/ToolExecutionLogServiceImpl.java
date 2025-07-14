package com.BE.service.implementServices;

import com.BE.enums.ExecutionStatus;
import com.BE.enums.ToolTypeEnum;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.ToolExecutionLogMapper;
import com.BE.model.entity.ToolExecutionLog;
import com.BE.model.request.KafkaData;
import com.BE.model.request.ToolExecutionLogRequest;
import com.BE.model.request.ToolExecutionLogSearchRequest;
import com.BE.model.request.ToolKafkaPayload;
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

    @Value("${kafka.topic.name.request}")
    String requestTopic;


    @Override
    @Transactional
    public ToolExecutionLogResponse save(ToolExecutionLogRequest request) {
        ToolExecutionLog log = mapper.toEntity(request);
        log.setUpdatedAt(dateNowUtils.getCurrentDateTimeHCM());
        log.setCreatedAt(dateNowUtils.getCurrentDateTimeHCM());
        log.setStatus(ExecutionStatus.PENDING);
        ToolExecutionLog saved = repository.save(log);
        ToolExecutionLogResponse response = mapper.toResponse(saved);
        if (request.getToolType().equals(ToolTypeEnum.EXTERNAL)) {

        } else {
            try {
                // Serialize log response thành JSON
                Map<String, Object> input = request.getInput();
                input.put("tool_log_id",response.getId());
                ToolKafkaPayload payload = ToolKafkaPayload.builder()
                        .type(request.getToolName())
                        .data(KafkaData.builder()
                                .user_id(request.getUserId().toString())
                                .lesson_id(request.getLessonId())
                                .lesson_plan_json(input)
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
    public void updateOutputByLogId(Long toolLogId, boolean success, Map<String, Object> output) {
        ToolExecutionLog log = repository.findById(toolLogId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy log với ID: " + toolLogId));

        // Cập nhật output và status
        log.setOutput(output);
        log.setStatus(success ? ExecutionStatus.SUCCESS : ExecutionStatus.FAILED);

        repository.save(log);

        
    }

}
