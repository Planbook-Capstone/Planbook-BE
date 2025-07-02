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

    @Schema(example = "Mathematics", description = "Tag name")
    @NotBlank(message = "Tag name is required")
    @Size(max = 100, message = "Tag name must not exceed 100 characters")
    String name;

    @Schema(example = "Resources related to mathematics", description = "Tag description")
    String description;
}
