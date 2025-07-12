package com.BE.service.implementServices;

import com.BE.model.entity.OutboxMessage;
import com.BE.repository.OutboxRepository;
import com.BE.service.interfaceServices.IOutboxService;
import com.BE.utils.DateNowUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class OutboxServiceImpl implements IOutboxService {

     OutboxRepository outboxRepository;
     DateNowUtils dateNowUtils;




    public void saveOutbox(String topic, String payload, String eventType) {
        OutboxMessage message = OutboxMessage.builder()
                .topic(topic)
                .payload(payload)
                .eventType(eventType)
                .createdAt(dateNowUtils.getCurrentDateTimeHCM())
                .kafkaSent(false)
                .build();

        outboxRepository.save(message);
    }
}

