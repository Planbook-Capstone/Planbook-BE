package com.BE.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterRequest {
    @Schema(
            description = "Tên của chương (ví dụ: 'Chương 1: Mở đầu', 'Chương 2: Đạo hàm')",
            example = "Chương 1: Đại cương về hóa học",
            required = true,
            type = "string"
    )
    @NotBlank(message = "Chapter name cannot be blank")
    String name;

    @Schema(
            description = "ID của cuốn sách mà chương này thuộc về",
            type = "integer",
            format = "int64",
            example = "1", // Ví dụ ID của Book
            required = true
    )
    @NotNull(message = "Book ID cannot be null")
    Long bookId; // Sử dụng Long để ánh xạ tới Book entity
}
