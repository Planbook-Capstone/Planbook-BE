package com.BE.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response DTO for Grade entity")
public class GradeResponse {
    @Schema(
            description = "ID của khối lớp",
            type = "integer",
            format = "int64",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Tên khối lớp",
            type = "string",
            example = "Grade 10"
    )
    private String name;

    @Schema(
            description = "Trạng thái hoạt động của khối lớp",
            type = "string",
            allowableValues = { "ACTIVE", "INACTIVE" },
            example = "ACTIVE"
    )
    private String status;

    @Schema(
            description = "Thời điểm tạo (ISO 8601)",
            type = "string",
            format = "date-time",
            example = "2025-06-01T12:00:00"
    )
    private String createdAt;

    @Schema(
            description = "Thời điểm cập nhật (ISO 8601)",
            type = "string",
            format = "date-time",
            example = "2025-06-02T09:30:00"
    )
    private String updatedAt;
}
