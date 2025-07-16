package com.BE.model.request;

import com.BE.enums.StatusEnum;
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

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SlideTemplateRequest {
    @NotBlank(message = "Name cannot be blank")
    String name;

    @NotBlank(message = "Description cannot be blank")
    String description;

    @NotNull(message = "TextBlocks không được để trống")
    @Schema(
            description = "TextBlocks lưu text",
            example = "{\"text\": \"Xin chào\"}"
    )
    Map<String, Object> textBlocks;

    @NotNull(message = "ImageBlocks không được để trống")
    @Schema(
            description = "ImageBlocks lưu link image",
            example = "{\"text\": \"Xin chào\"}"
    )
    Map<String, Object> imageBlocks;
}
