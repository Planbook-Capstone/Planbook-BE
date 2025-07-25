package com.BE.model.response;


import com.BE.enums.GatewayEnum;
import com.BE.enums.StatusEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentTransactionResponse {
    UUID id;
    UUID orderId;
    BigDecimal amount;
    StatusEnum status;
    UUID parentTransactionId;
    Long payosOrderCode;
    GatewayEnum gateway;
    String checkoutUrl;
    String qrCode;
    String payosTransactionId;
    String description;
    String failureReason;
    Map<String, Object> webhookPayload;
    LocalDateTime expiredAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
