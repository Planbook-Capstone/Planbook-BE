package com.BE.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class WalletTokenRequest {

    @Schema(description = "ID người dùng", example = "aa9f1252-382e-4e70-b5d5-6d5b5d20cacc")
    @NotNull(message = "userId không được để trống")
    private UUID userId;

    private String description;

    @Schema(description = "Số token bị trừ", example = "10")
    @NotNull(message = "Số token không được để trống")
    @Min(value = 0, message = "Số token phải từ 0 trở lên")
    private Integer amount;
}

