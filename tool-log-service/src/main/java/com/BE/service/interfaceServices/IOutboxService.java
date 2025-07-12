package com.BE.service.interfaceServices;

public interface IOutboxService {
    void saveOutbox(String topic, String payload, String eventType);
}
