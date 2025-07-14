package com.BE.controller;

import com.BE.service.interfaceServices.IToolExecutionLogService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class ToolLogKafkaConsumer {

    private final IToolExecutionLogService logService;

    @KafkaListener(topics = "${kafka.topic.name.response}", groupId = "tool-log-group")
    public void listen(String message) {
        log.info("📥 Nhận message từ Kafka: {}", message);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(message);

            JsonNode outerData = root.path("data");
            JsonNode innerData = outerData.path("data");

            boolean success = innerData.path("success").asBoolean(false);
            JsonNode resultNode = innerData.path("result");

            if (success) {
                JsonNode lessonPlanNode = resultNode.path("lesson_plan");
                String toolLogIdStr = lessonPlanNode.path("tool_log_id").asText(null);

                if (toolLogIdStr == null) {
                    log.warn("⚠️ Không tìm thấy tool_log_id trong lesson_plan");
                    return;
                }

                Long toolLogId = Long.parseLong(toolLogIdStr);
                Map<String, Object> lessonPlanMap = mapper.convertValue(lessonPlanNode, new TypeReference<>() {});

                logService.updateOutputByLogId(toolLogId, true, lessonPlanMap);
                log.info("✅ Cập nhật thành công output SUCCESS cho tool_log_id: {}", toolLogId);

            } else {
                // Nếu thất bại, lấy error từ result
                String toolLogIdStr = resultNode.path("lesson_plan").path("tool_log_id").asText(null);
                if (toolLogIdStr == null) {
                    log.warn("⚠️ Không tìm thấy tool_log_id trong lesson_plan (khi thất bại)");
                    return;
                }

                Long toolLogId = Long.parseLong(toolLogIdStr);
                String errorMessage = resultNode.path("error").asText("Không rõ lỗi");

                logService.updateOutputByLogId(toolLogId, false, Map.of("error", errorMessage));
                log.info("❌ Cập nhật output FAILED với lỗi: {}", errorMessage);
            }

        } catch (Exception e) {
            log.error("❌ Lỗi khi xử lý message Kafka: {}", e.getMessage(), e);
        }
    }

}
