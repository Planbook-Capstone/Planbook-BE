package com.BE.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class AcademicResourceSearchRequest {

    @Schema(example = "math", description = "Search keyword for name or description")
    String keyword;

    @Schema(example = "image", description = "Filter by resource type")
    String type;

    @Schema(example = "[1, 2]", description = "Filter by tag IDs")
    Set<Long> tagIds;

    @Schema(example = "0", description = "Page number (0-based)")
    Integer page = 0;

    @Schema(example = "10", description = "Page size")
    Integer size = 10;

    @Schema(example = "createdAt", description = "Sort field: name, createdAt, updatedAt")
    String sortBy = "createdAt";

    @Schema(example = "desc", description = "Sort direction: asc, desc")
    String sortDirection = "desc";
}
