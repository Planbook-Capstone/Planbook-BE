package com.BE.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request DTO để tạo hoặc cập nhật thông tin Loại Sách")
public class BookTypeRequest {


    @Schema(
            description = "Tên của công cụ hỗ trợ (ví dụ: 'Chấm điểm', 'Tạo giáo án', 'Tạo slide bài giảng', 'Tạo đề thi')", // ĐÃ THAY ĐỔI MÔ TẢ
            example = "Chấm điểm", // ĐÃ THAY ĐỔI VÍ DỤ
            required = true,
            type = "string"
    )
    @NotBlank(message = "BookType name cannot be blank")
    @Size(max = 100, message = "BookType name must be less than or equal to 100 characters")
    String name;

    @Schema(
            description = "Mô tả chi tiết về công cụ hỗ trợ", // ĐÃ THAY ĐỔI MÔ TẢ
            example = "Công cụ giúp giảng viên quản lý và chấm điểm bài tập, bài kiểm tra của sinh viên.", // ĐÃ THAY ĐỔI VÍ DỤ
            type = "string",
            nullable = true
    )
    String description;

    @Schema(
            description = "Mức độ ưu tiên", // ĐÃ THAY ĐỔI MÔ TẢ
            example = "1", // ĐÃ THAY ĐỔI VÍ DỤ
            required = true,
            type = "int"
    )

    String href;

    @NotNull(message = "Priority cannot be null")
    @Min(value = 1, message = "Priority must be greater than or equal to 1") // Đảm bảo giá trị ưu tiên là số nguyên dương
    int priority;

    @Schema(
            description = "Icon đại diện cho công cụ hỗ trợ, có thể là base64 encoded string", // ĐÃ THAY ĐỔI MÔ TẢ
            example = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==",
            type = "string",
            nullable = true
    )
    String icon;

    // THÊM TRƯỜNG MỚI NÀY
    @Schema(
            description = "Số lượng token bị trừ cho mỗi lượt hỏi khi sử dụng công cụ này. Phải là số nguyên không âm.",
            example = "10", // Đặt ví dụ phù hợp
            required = true, // Hoặc false tùy theo yêu cầu của bạn
            type = "integer",
            format = "int32"
    )
    @NotNull(message = "Token cost per query cannot be null") // Nếu bạn muốn nó bắt buộc
    @Min(value = 0, message = "Token cost per query must be a non-negative integer") // Đảm bảo giá trị không âm
            Integer tokenCostPerQuery;
}