package com.BE.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class AcademicResourceUpdateRequest {

    @Schema(example = "image", description = "Resource type: image, gif, video, webp, iframe")
    @Size(max = 20, message = "Type must not exceed 20 characters")
    String type;

    @Schema(example = "Updated Math Formula Collection", description = "Resource name")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    String name;

    @Schema(example = "Updated collection of important math formulas", description = "Resource description")
    String description;

    @Schema(example = "https://example.com/updated-resource.pdf", description = "Resource URL or file path")
    String url;

    @Schema(example = "[1, 2, 4]", description = "List of tag IDs to associate with this resource")
    Set<Long> tagIds;
}
