package com.BE.model.response;

import com.BE.enums.ExamInstanceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamInstanceResponse {
    
    private UUID id;
    private UUID templateId;
    private String templateName;
    private String code;
    private String description;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String excelUrl;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private Integer durationMinutes;
    private String subject;
    private Integer grade;

    @Schema(
        description = """
            Current status of the exam instance. Determines student access and available operations.

            **Status Meanings:**
            - **DRAFT**: Exam is being prepared, not accessible to students
            - **SCHEDULED**: Exam is scheduled, waiting for start time
            - **ACTIVE**: Exam is live, students can access and submit
            - **PAUSED**: Exam is temporarily suspended
            - **COMPLETED**: Exam has ended, results available
            - **CANCELLED**: Exam has been cancelled permanently
            """,
        example = "ACTIVE",
        implementation = ExamInstanceStatus.class
    )
    private ExamInstanceStatus status;

    @Schema(
        description = "Timestamp when the status was last changed. Null if status has never been changed from initial DRAFT.",
        example = "2024-01-15T07:45:00",
        nullable = true
    )
    private LocalDateTime statusChangedAt;

    @Schema(
        description = """
            Reason provided for the last status change. Used for audit trail and understanding
            why status changes were made. Null if no reason was provided or status has never been changed.
            """,
        example = "Starting exam early because all students are ready",
        nullable = true,
        maxLength = 500
    )
    private String statusChangeReason;
}
