package com.BE.model.request;

import com.BE.enums.PlaceholderTypeEnum;
import com.BE.exception.EnumValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SlidePlaceholderRequest {
    
    @NotNull(message = "Type cannot be null")
    @EnumValidator(enumClass = PlaceholderTypeEnum.class, message = "Invalid placeholder type")
    @Enumerated(EnumType.STRING)
    @Schema(description = "Loại placeholder", example = "LessonName")
    PlaceholderTypeEnum type;

    @NotBlank(message = "Name cannot be blank")
    @Schema(description = "Tên placeholder", example = "Tên bài học")
    String name;

    @NotBlank(message = "Description cannot be blank")
    @Schema(description = "Mô tả placeholder", example = "Tên của bài học được hiển thị trên slide")
    String description;
}
