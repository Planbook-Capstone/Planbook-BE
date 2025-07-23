package com.BE.model.entity;


import com.BE.config.MapToJsonConverter;
import com.BE.enums.GatewayEnum;
import com.BE.enums.PaymentStatusEnum;
import com.BE.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "payment_transaction")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    Order order;

    @Column(nullable = false)
    BigDecimal amount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    StatusEnum status;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    String description;


    @Column(name = "parent_transaction_id")
    UUID parentTransactionId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    GatewayEnum gateway;

    @Column(name = "payos_order_code", nullable = false)
    Long payosOrderCode;

    String checkoutUrl;

    @Column(unique = true)
    String payosTransactionId;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    String failureReason;


    @Column(name = "webhook_payload", columnDefinition = "JSON")
    @Convert(converter = MapToJsonConverter.class)
    Map<String, Object> webhookPayload;

    LocalDateTime expiredAt;

    @Builder.Default
    @CreationTimestamp
    LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @UpdateTimestamp
    LocalDateTime updatedAt = LocalDateTime.now();

}
