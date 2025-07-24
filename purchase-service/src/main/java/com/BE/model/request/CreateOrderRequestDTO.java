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

    @NotNull(message = "priority không được null")
    private int priority;
}

