package com.BE.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TagCreateRequest {

    @Schema(example = "Toán học", description = "Tên loại học liệu")
    @NotBlank(message = "Tên loại học liệu là bắt buộc")
    @Size(max = 100, message = "Tên loại học liệu không được vượt quá 100 ký tự")
    String name;

    @Schema(example = "Tài nguyên liên quan đến toán học", description = "Mô tả loại học liệu")
    String description;
}
