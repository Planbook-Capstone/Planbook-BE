package com.BE.model.request;

import com.BE.enums.AcademicResourceEnum;
import com.BE.enums.SortBy;
import com.BE.enums.SortDirection;
import com.BE.exception.EnumValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcademicResourceSearchRequest {

    @Schema(
            description = "ID của bài học",
            example = "1"
    )
    Long lessonId;

    @Schema(
            description = "ID của của teacher",
            example = "c5f52e11-23ac-4b4a-8472-835b556be64e"
    )
    UUID createdBy;

    @Schema(example = "math", description = "Search keyword for name or description")
    String keyword;

    @Schema(example = "image", description = "Filter by resource type")
    String type;

    @Schema(example = "[1, 2]", description = "Filter by tag IDs")
    Set<Long> tagIds;

    @Schema(example = "1", description = "Page number (1-based)")
    Integer page = 1;

    @Schema(example = "10", description = "Page size")
    Integer size = 10;

    @EnumValidator(enumClass = SortBy.class, message = "sortBy phải là một trong các giá trị hợp lệ")
    @Schema(description = "Trường sắp xếp")
    SortBy sortBy = SortBy.CREATED_AT;

    @EnumValidator(enumClass = SortDirection.class, message = "sortDirection phải là ASC hoặc DESC")
    @Schema(description = "Hướng sắp xếp (asc/desc)")
    SortDirection sortDirection = SortDirection.DESC;

    @EnumValidator(enumClass = AcademicResourceEnum.class, message = "visibility phải là INTERNAL hoặc EXTERNAL")
    @Schema(description = "Phạm vi hiển thị (INTERNAL, EXTERNAL)", example = "INTERNAL")
    AcademicResourceEnum visibility;
}
