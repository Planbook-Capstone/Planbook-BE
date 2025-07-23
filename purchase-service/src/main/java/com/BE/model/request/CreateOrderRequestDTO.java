package com.BE.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateOrderRequestDTO {
    @NotNull(message = "packageId không được null")
    private UUID packageId;

    @NotNull(message = "amount không được null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Số tiền phải lớn hơn 0")
    private BigDecimal amount;


}

