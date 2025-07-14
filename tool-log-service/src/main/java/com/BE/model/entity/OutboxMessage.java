package com.BE.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Column(name = "event_type", nullable = false)
    String eventType;

    @Column(name = "aggregate_id", nullable = false)
    String aggregateId;

    @Column(name = "kafka_sent", nullable = false)
    boolean kafkaSent = false;

    @Column(nullable = false)
    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;
}
