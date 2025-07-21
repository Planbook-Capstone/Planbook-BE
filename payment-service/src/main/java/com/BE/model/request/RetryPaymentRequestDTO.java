package com.BE.model.request;


import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RetryPaymentRequestDTO {

    @NotNull(message = "Mã đơn hàng không được để trống")
    UUID orderId;

}
