package com.BE.controller;

import com.BE.service.interfaceServices.IKafkaProcessingService;
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

    private final IKafkaProcessingService kafkaProcessingService;

    @KafkaListener(topics = "${kafka.topic.name.response}", groupId = "tool-log-group")
    public void listen(String message) {
        log.info("📥 Received message from Kafka: {}", message);
        kafkaProcessingService.process(message);
    }

}
