package com.BE.model.response;

import com.BE.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionResponse {
    UUID id;
    UUID orderId;
    Integer tokenBefore;
    Integer tokenChange;
    TransactionType type;
    String description;
    LocalDateTime createdAt;
}
