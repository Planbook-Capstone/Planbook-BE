package com.BE.model.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {
    UUID id;
    Integer balance;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<WalletTransactionResponse> transactions;
}
