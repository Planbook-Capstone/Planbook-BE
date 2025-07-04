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
public class LessonRequest {

    @Schema(
            description = "Tên của bài học (ví dụ: 'Bài 1: Khái niệm tập hợp', 'Bài 3: Lực hấp dẫn')",
            example = "Bài 1: Cấu tạo nguyên tử",
            required = true,
            type = "string"
    )
    @NotBlank(message = "Lesson name cannot be blank")
    String name;

    @Schema(
            description = "ID của chương mà bài học này thuộc về",
            type = "integer",
            format = "int64",
            example = "1", // Ví dụ ID của Chapter
            required = true
    )
    @NotNull(message = "Chapter ID cannot be null")
    Long chapterId; // Sử dụng Long để ánh xạ tới Chapter entity

}
