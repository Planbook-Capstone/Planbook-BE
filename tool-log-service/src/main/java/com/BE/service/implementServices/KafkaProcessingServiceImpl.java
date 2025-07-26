package com.BE.service.implementServices;

import com.BE.model.request.ToolLogUpdateRequest;
import com.BE.model.request.WebSocketMessageRequest;
import com.BE.model.response.ToolExecutionLogResponse;
import com.BE.service.interfaceServices.IKafkaProcessingService;
import com.BE.service.interfaceServices.IToolExecutionLogService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProcessingServiceImpl implements IKafkaProcessingService {

    private final ObjectMapper mapper;
    private final IToolExecutionLogService logService;

    @Override
    public void process(String rawMessage) {
        try {
            JsonNode root = mapper.readTree(rawMessage);
            String type = root.path("data").path("type").asText();
            JsonNode innerData = root.path("data").path("data");
            System.out.println(type);
            switch (type) {
                case "lesson_plan_content_generation_response" -> handleAcceptedResponse(innerData);
                case "lesson_plan_content_generation_result" -> handleResultMessage(innerData);
                case "lesson_plan_content_generation_progress" -> sendProgressWebSocket(innerData);
                default -> log.warn("⚠️ Không hỗ trợ type message Kafka: {}", type);
            }
        } catch (Exception e) {
            log.error("❌ Lỗi khi xử lý Kafka message: {}", e.getMessage(), e);
        }
    }

    private void handleResultMessage(JsonNode innerData) {
        try {
            boolean success = innerData.path("success").asBoolean(false);
            JsonNode resultNode = innerData.path("result");
            JsonNode outputNode = resultNode.path("output");
            String toolLogIdStr = innerData.path("tool_log_id").asText(null);
            if (toolLogIdStr == null) {
                log.warn("⚠️ Không tìm thấy tool_log_id trong lesson_plan");
                return;
            }

            Long toolLogId = Long.parseLong(toolLogIdStr);

            if (success) {
                Map<String, Object> outputMap = mapper.convertValue(outputNode, new TypeReference<>() {});
                ToolLogUpdateRequest request = new ToolLogUpdateRequest(true, outputMap);
                logService.updateOutputByLogId(toolLogId, request);
                log.info("✅ Cập nhật output SUCCESS cho tool_log_id: {}", toolLogId);
            } else {
                String errorMessage = resultNode.path("error").asText("Không rõ lỗi");
                ToolLogUpdateRequest request = new ToolLogUpdateRequest(false, Map.of("error", errorMessage));
                logService.updateOutputByLogId(toolLogId, request);
                log.info("❌ Cập nhật output FAILED với lỗi: {}", errorMessage);
            }

        } catch (Exception e) {
            log.error("❌ Lỗi khi xử lý kết quả lesson plan: {}", e.getMessage(), e);
        }
    }

    private void handleAcceptedResponse(JsonNode innerData) {
        try {

            Long id = innerData.path("tool_log_id").asLong();
            ToolExecutionLogResponse toolExecutionLogResponse = logService.getById(id);
            String userId = toolExecutionLogResponse.getUserId().toString();
            String taskId = innerData.path("task_id").asText(null);
            String message = innerData.path("message").asText("Task accepted");

            if (userId == null || taskId == null) {
                log.warn("⚠️ Missing user_id or task_id in accepted response");
                return;
            }

            WebSocketMessageRequest request = WebSocketMessageRequest.builder()
                    .userId(userId)
                    .destination("/queue/notifications")
                    .payload(Map.of(
                            "type", "accepted",
                            "task_id", taskId,
                            "message", message
                    ))
                    .build();

            logService.sendWebSocket(request);
            log.info("📤 Sent ACCEPTED task to user {} with task_id {}", userId, taskId);

        } catch (Exception e) {
            log.error("❌ Error handling accepted response: {}", e.getMessage(), e);
        }
    }


    private void sendProgressWebSocket(JsonNode innerData) {
        try {
            Long id = innerData.path("tool_log_id").asLong();
            ToolExecutionLogResponse toolExecutionLogResponse = logService.getById(id);
            if (toolExecutionLogResponse.getId() == null) {
                log.warn("⚠️ Không tìm thấy user_id trong progress message");
                return;
            }

            Map<String, Object> payload = Map.of(
                    "type", "progress",
                    "task_id", innerData.path("task_id").asText(),
                    "lesson_id", innerData.path("lesson_id").asLong(),
                    "progress", innerData.path("progress").asInt(),
                    "status", innerData.path("status").asText(),
                    "message", innerData.path("message").asText()
            );

            WebSocketMessageRequest request = WebSocketMessageRequest.builder()
                    .userId(toolExecutionLogResponse.getUserId().toString())
                    .destination("/queue/notifications")
                    .payload(payload)
                    .build();

            logService.sendWebSocket(request);
            log.info("📤 Đã gửi tiến trình {}% cho user {}", innerData.path("progress").asInt(), toolExecutionLogResponse.getId());

        } catch (Exception ex) {
            log.warn("⚠️ Lỗi khi gửi WebSocket tiến trình: {}", ex.getMessage());
        }
    }
}
