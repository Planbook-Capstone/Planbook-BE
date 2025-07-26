package com.BE.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new exam instance from a template")
public class CreateExamInstanceRequest {

    @Schema(description = "ID of the exam template to use",
        example = "550e8400-e29b-41d4-a716-446655440001", required = true)
    @NotNull(message = "ID mẫu đề thi là bắt buộc")
    private UUID templateId;

    @Schema(description = "Description of this exam instance",
        example = "Math test for class 10A - Final exam")
    private String description;

    @Schema(description = "Start time of the exam (ISO 8601 format)",
        example = "2024-01-15T08:00:00", required = true)
    @NotNull(message = "Thời gian bắt đầu là bắt buộc")
    @Future(message = "Thời gian bắt đầu phải trong tương lai")
    private LocalDateTime startAt;

    @Schema(description = "End time of the exam (ISO 8601 format)",
        example = "2024-01-15T09:00:00", required = true)
    @NotNull(message = "Thời gian kết thúc là bắt buộc")
    @Future(message = "Thời gian kết thúc phải trong tương lai")
    private LocalDateTime endAt;
}
