package com.BE.model.request;

import com.BE.enums.FormStatusEnum;
import com.BE.enums.StatusEnum;
import com.BE.exception.EnumValidator;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
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
public class FormRequest {
    @NotBlank(message = "Name cannot be blank")
    String name;
    @NotBlank(message = "Description cannot be blank")
    String description;
    @Schema(example = "DRAFT, PUBLISHED, DELETE", description = "Form Status Enum")
    @EnumValidator(enumClass = FormStatusEnum.class, message = "Invalid status value")
    @Enumerated(EnumType.STRING)
    FormStatusEnum status;
    JsonNode formData;

}
