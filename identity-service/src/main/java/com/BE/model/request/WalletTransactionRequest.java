package com.BE.model.request;

import com.BE.enums.TransactionType;
import com.BE.exception.EnumValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionRequest {

    @NotNull(message = "Số token được nạp không được để trống")
    @Min(value = 1, message = "Số token phải lớn hơn 0")
    Integer tokenChange;

    @Schema(example = "RECHARGE, REWARD, REFUND", description = "TransactionType Enum")
    @EnumValidator(enumClass = TransactionType.class, message = "Loại giao dịch không được để trống")
    @Enumerated(EnumType.STRING)
    TransactionType type;

    @NotBlank(message = "Mô tả giao dịch không được để trống")
    String description;

    @NotNull(message = "Id Giao dịch được nạp không được để trống")
    UUID orderId;

}
