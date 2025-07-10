package com.BE.utils;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageConsumer {
    @KafkaListener(topics = "${kafka.topic.name:my-default-topic}", groupId = "my-consumer-group")
    public void listen(ConsumerRecord<String, String> record) {
        System.out.println("Received message: " + record);
        // Xử lý message ở đây
    }
}
