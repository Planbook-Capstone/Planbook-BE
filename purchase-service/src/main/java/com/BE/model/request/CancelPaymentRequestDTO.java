package com.BE.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
public class CancelPaymentRequestDTO {

    @Schema(description = "ID đơn hàng trong hệ thống (UUID)", example = "f56e2d27-02a6-4d6b-a48f-15241ce3cc04", required = true)
    private UUID orderId;

    @Schema(description = "Lý do huỷ đơn hàng", example = "Khách đổi ý hoặc huỷ mua", required = false)
    private String cancellationReason;
}
