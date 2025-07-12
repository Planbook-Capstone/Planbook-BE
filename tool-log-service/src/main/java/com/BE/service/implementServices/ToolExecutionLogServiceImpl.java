package com.BE.service.implementServices;

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
        log.setCreatedAt(dateNowUtils.getCurrentDateTimeHCM());

        ToolExecutionLog saved = repository.save(log);
        ToolExecutionLogResponse response = mapper.toResponse(saved);

        try {
            // Serialize log response thành JSON
            Object lessonPlanJson = objectMapper.readValue(request.getInputJson(), Object.class); // parse string -> object

            ToolKafkaPayload payload = ToolKafkaPayload.builder()
                    .type(request.getToolName())
                    .data(
                            KafkaData.builder()
                                    .user_id(request.getUserId().toString())
                                    .lesson_id(request.getLessonId().toString())
                                    .lesson_plan_json(lessonPlanJson)
                                    .timestamp(Instant.now().toString())
                                    .build()
                    )
                    .build();

            String jsonToSend = objectMapper.writeValueAsString(payload);

            iOutboxService.saveOutbox(requestTopic, jsonToSend, response.getToolName());

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi serialize ToolExecutionLog khi ghi outbox", e);
        }

        return response;
    }

    @Override
    public Page<ToolExecutionLogResponse> getAll(ToolExecutionLogSearchRequest request) {
        pageUtil.checkOffset(request.getOffset());
        Pageable pageable = pageUtil.getPageable(request.getOffset() - 1, request.getPageSize(), request.getSortBy(), request.getSortDirection());

        Specification<ToolExecutionLog> spec = Specification.where(null);

        if (request.getSearch() != null && !request.getSearch().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("input")), "%" + request.getSearch().toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("output")), "%" + request.getSearch().toLowerCase() + "%")
            ));
        }

        if (request.getToolType() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("toolType"), request.getToolType()));
        }

        if (request.getUserId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("userId"), request.getUserId()));
        }

        return repository.findAll(spec, pageable).map(mapper::toResponse);
    }
}
