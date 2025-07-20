package com.BE.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new exam template")
public class CreateExamTemplateRequest {

    @Schema(description = "Name of the exam", example = "Math Basic Test", required = true)
    @NotBlank(message = "Exam name is required")
    private String name;

    @Schema(description = "Subject of the exam", example = "Mathematics", required = true)
    @NotBlank(message = "Subject is required")
    private String subject;

    @Schema(description = "Grade level", example = "10", minimum = "1", maximum = "12", required = true)
    @NotNull(message = "Grade is required")
    @Min(value = 1, message = "Grade must be at least 1")
    private Integer grade;

    @Schema(description = "Duration of exam in minutes", example = "60", minimum = "1", required = true)
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    @Schema(description = "Exam content with questions and answers", required = true)
    @NotNull(message = "Content JSON is required")
    private Map<String, Object> contentJson;

    @Schema(description = "Grading configuration for each part",
            example = "{\"PHẦN I\": 0.25, \"PHẦN II\": 0.5, \"PHẦN III\": 1.0}")
    private Map<String, Double> gradingConfig;

    @Schema(description = "Total exam score", example = "10.0")
    private Double totalScore = 10.0;
}
