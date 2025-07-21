package com.BE.model.request;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePaymentRequestDTO {

    @NotNull(message = "ID đơn hàng không được để trống")
    UUID orderId;

    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Số tiền phải lớn hơn 0")
    BigDecimal amount;

    @NotBlank(message = "Mô tả không được để trống")
    String description;
}

