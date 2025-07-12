package com.BE.controller;

import com.BE.service.interfaceServices.IToolExecutionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.KafkaListener;


@Service
@RequiredArgsConstructor
@Slf4j
public class ToolLogKafkaConsumer {

    private final IToolExecutionLogService logService;

    @KafkaListener(topics = "${kafka.topic.name.response}", groupId = "tool-log-group")
    public void listen(String message) {
        log.info("Nhận message từ Kafka: {}", message);
        try {
            // Parse message và gọi logService.updateOutput()
        } catch (Exception e) {
            log.error("Lỗi xử lý message: {}", e.getMessage(), e);
        }
    }
}
