package com.BE.utils;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageConsumer {
    @KafkaListener(topics = "${kafka.topic.name:my-default-topic}", groupId = "my-consumer-group")
    public void listen(String message) {
        System.out.println("Received message: " + message);
        // Xử lý message ở đây
    }
}
