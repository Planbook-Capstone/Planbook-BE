package com.BE.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CancelPaymentResponseDTO {

    @Schema(description = "Tổng số giao dịch đã huỷ", example = "2")
    private int cancelledCount;

    @Schema(description = "Thông điệp trạng thái", example = "Đã huỷ toàn bộ các giao dịch PENDING")
    private String message;
}
