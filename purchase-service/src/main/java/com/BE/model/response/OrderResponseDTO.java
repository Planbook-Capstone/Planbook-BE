package com.BE.model.response;

import com.BE.enums.StatusEnum;
import com.BE.model.entity.OrderHistory;
import com.BE.model.entity.SubscriptionPackage;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponseDTO {
    UUID id;
    UUID userId;
    BigDecimal amount;
    StatusEnum status;
    String checkoutUrl;
    String qrCode;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    SubscriptionPackage subscriptionPackage;
    List<OrderHistoryResponseDTO> orderHistories;
    List<PaymentTransactionResponse> transactions;

}
