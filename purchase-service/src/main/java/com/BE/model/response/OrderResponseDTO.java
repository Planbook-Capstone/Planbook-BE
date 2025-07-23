package com.BE.model.response;

import com.BE.enums.StatusEnum;
import com.BE.model.entity.OrderHistory;
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
    UUID packageId;
    BigDecimal amount;
    StatusEnum status;
    String checkoutUrl;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<OrderHistoryResponseDTO> orderHistories;
    List<PaymentTransactionResponse> transactions;

}
