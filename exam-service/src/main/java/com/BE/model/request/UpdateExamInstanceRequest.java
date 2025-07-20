package com.BE.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update an existing exam instance")
public class UpdateExamInstanceRequest {

    @Schema(description = "Updated description of this exam instance",
        example = "Math test for class 10A - Midterm exam")
    private String description;

    @Schema(description = "Updated start time of the exam (ISO 8601 format)",
        example = "2024-01-15T08:00:00")
    private LocalDateTime startAt;

    @Schema(description = "Updated end time of the exam (ISO 8601 format)",
        example = "2024-01-15T09:00:00")
    private LocalDateTime endAt;
}
