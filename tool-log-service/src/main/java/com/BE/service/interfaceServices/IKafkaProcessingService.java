package com.BE.service.interfaceServices;

public interface IKafkaProcessingService {
    void process(String rawMessage);
}
