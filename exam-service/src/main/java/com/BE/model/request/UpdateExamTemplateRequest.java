package com.BE.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update an existing exam template")
public class UpdateExamTemplateRequest {

    @Schema(description = "Name of the exam", example = "Math Advanced Test")
    private String name;

    @Schema(description = "Subject of the exam", example = "Mathematics")
    private String subject;

    @Schema(description = "Grade level", example = "10", minimum = "1", maximum = "12")
    @Min(value = 1, message = "Grade must be at least 1")
    private Integer grade;

    @Schema(description = "Duration of exam in minutes", example = "90", minimum = "1")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    @Schema(description = "Updated school name", example = "THPT Nguyen Hue")
    private String school;

    @Schema(description = "Updated exam code", example = "1234")
    private String examCode;

    @Schema(description = "Updated atomic masses information", example = "H=1, C=12, O=16")
    private String atomicMasses;

    @Schema(description = "Updated exam content with questions and answers")
    private Map<String, Object> contentJson;

    @Schema(description = "Updated scoring configuration for the exam",
            example = "{\"useStandardScoring\": false, \"part1Score\": 0.25, \"part2ScoringType\": \"manual\", \"part2CustomScore\": 4, \"part2ManualScores\": {\"1\": 0.1, \"2\": 0.25, \"3\": 3, \"4\": 10}, \"part3Score\": 0.25}")
    private Map<String, Object> scoringConfig;

    @Schema(description = "Updated total exam score", example = "10.0")
    private Double totalScore;
}
