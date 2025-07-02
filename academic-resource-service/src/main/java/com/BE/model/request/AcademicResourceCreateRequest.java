package com.BE.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcademicResourceCreateRequest {

    @Schema(example = "image", description = "Resource type: image, gif, video, webp, iframe")
    @NotBlank(message = "Type is required")
    @Size(max = 20, message = "Type must not exceed 20 characters")
    String type;

    @Schema(example = "Math Formula Collection", description = "Resource name")
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    String name;

    @Schema(example = "Collection of important math formulas for grade 10", description = "Resource description")
    String description;

    @Schema(example = "https://example.com/resource.pdf", description = "Resource URL or file path")
    String url; // Optional if file is being uploaded

    @Schema(example = "[1, 2, 3]", description = "List of tag IDs to associate with this resource")
    Set<Long> tagIds;
}
