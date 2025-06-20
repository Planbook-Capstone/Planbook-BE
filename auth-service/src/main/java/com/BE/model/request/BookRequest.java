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
public class BookRequest {

    @Schema(
            description = "Tên của cuốn sách (ví dụ: 'Sách giáo khoa Toán 10', 'Bài tập Vật lý 11')",
            example = "Sách giáo khoa Toán 10",
            required = true,
            type = "string"
    )
    @NotBlank(message = "Book name cannot be blank")
    String name;

    @Schema(
            description = "ID của môn học mà cuốn sách này thuộc về",
            type = "integer",
            format = "int64",
            example = "1", // Ví dụ ID của Subject
            required = true
    )
    @NotNull(message = "Subject ID cannot be null")
    Long subjectId; // Sử dụng Long để ánh xạ tới Subject entity
}
