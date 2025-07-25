package com.BE.model.response;


import com.BE.model.entity.WalletTransaction;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.time.LocalDateTime;

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
