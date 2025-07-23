package com.BE.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    // Tên topic có thể được lấy từ application.properties
    @Value("${kafka.topic.name.request:my-default-topic}") // Sử dụng cùng biến này
    private String topicName;

    @Autowired
    public KafkaMessageProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message) {
        kafkaTemplate.send(topicName, message);
        System.out.println("Sent message: " + message + " to topic: " + topicName);
    }

    // Nếu bạn muốn gửi tin nhắn tới một topic cụ thể không phải topic mặc định
    public void sendMessageToTopic(String topic, String message) {
        kafkaTemplate.send(topic, message);
        System.out.println("Sent message: " + message + " to topic: " + topic);
    }
}
