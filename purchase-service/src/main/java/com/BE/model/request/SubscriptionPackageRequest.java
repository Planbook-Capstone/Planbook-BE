package com.BE.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionPackageRequest {

    @NotBlank(message = "Tên gói không được để trống")
    String name;

    @NotNull(message = "Số lượng token là bắt buộc")
    @Min(value = 1, message = "Số lượng token phải lớn hơn hoặc bằng 1")
    Integer tokenAmount;

    @NotNull(message = "Giá tiền là bắt buộc")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá tiền phải lớn hơn 0")
    BigDecimal price;

    @NotBlank(message = "Mô tả không được để trống")
    String description;

    boolean highlight;

    @NotNull(message = "priority không được null")
    Integer priority;

    @Schema(
            description = "Danh sách các chức năng theo thứ tự",
            example = """
                    {
                      "1": "Truy cập ưu tiên vào AI",
                      "2": "Hỗ trợ khách hàng qua email",
                      "3": "Lịch sử trò chuyện giới hạn"
                    }
                    """
    )
    @NotNull(message = "Trường features không được để trống")
    Map<String, String> features;
}
