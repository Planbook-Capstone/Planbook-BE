package com.BE.model.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    @NotNull(message = "Danh sách tính năng không được để trống")
    @Size(min = 1, message = "Phải có ít nhất một tính năng")
    @Builder.Default
    List<@NotBlank(message = "Mô tả tính năng không được để trống") String> features = new ArrayList<>();
}
