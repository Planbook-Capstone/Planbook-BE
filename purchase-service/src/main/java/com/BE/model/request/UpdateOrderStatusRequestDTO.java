package com.BE.model.request;

import com.BE.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequestDTO {
    @Schema(
            description = "Trạng thái mới cần cập nhật. Các giá trị hợp lệ: PENDING, SUCCESS, FAILED.",
            example = "SUCCESS"
    )
    @NotNull(message = "Trạng thái mới không được null")
    private StatusEnum status;

    @Schema(
            description = "Ghi chú tuỳ chọn cho việc cập nhật trạng thái",
            example = "Thanh toán thành công qua PayOS webhook"
    )
    private String note;
}
