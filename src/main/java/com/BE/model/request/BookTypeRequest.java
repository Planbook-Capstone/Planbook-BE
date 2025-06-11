package com.BE.model.request;

import jakarta.validation.constraints.NotBlank;
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
            description = "Tên của loại sách (ví dụ: 'Sách giáo khoa', 'Sách bài tập', 'Truyện')",
            example = "Sách giáo khoa",
            required = true,
            type = "string"
    )
    @NotBlank(message = "BookType name cannot be blank")
    @Size(max = 100, message = "BookType name must be less than or equal to 100 characters")
    String name;

    @Schema(
            description = "Mô tả chi tiết về loại sách",
            example = "Loại sách dùng cho việc học tập chính khóa trên lớp.",
            type = "string",
            nullable = true
    )
    String description;

    @Schema(
            description = "Icon đại diện cho loại sách, có thể là base64 encoded string",
            example = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==",
            type = "string",
            nullable = true
    )
    String icon;
}