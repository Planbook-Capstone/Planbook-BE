package com.BE.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tool_outbox")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxMessage {

    @Id
    @GeneratedValue
    UUID id;

    @Column(nullable = false)
    String topic;

    @Column(nullable = false, columnDefinition = "TEXT")
    String payload;

    @Column(nullable = false)
    String eventType;

    @Column(name = "kafka_sent", nullable = false)
    boolean kafkaSent = false;

    @Column(nullable = false)
    LocalDateTime createdAt;
}
