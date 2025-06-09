package com.BE.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request DTO để tạo hoặc cập nhật thông tin Môn học")

public class SubjectRequest {

    @Schema(
            description = "Tên của môn học (ví dụ: 'Toán học', 'Vật lý')",
            example = "Toán học",
            required = true,
            type = "string"
    )
    @NotBlank(message = "Subject name cannot be blank")
    String name;

    @Schema(
            description = "ID của khối lớp mà môn học này thuộc về",
            type = "integer",
            format = "int64",
            example = "1", // Ví dụ ID của Grade
            required = true
    )
    @NotNull(message = "Grade ID cannot be null")
    Long gradeId; // Sử dụng Long để ánh xạ tới Grade entity

}
