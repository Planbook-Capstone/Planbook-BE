package com.BE.model.request;

import com.BE.enums.ExamInstanceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = """
        Request to change exam instance status. This allows teachers to control the exam lifecycle
        by transitioning between different states such as starting early, pausing, resuming, or cancelling exams.
        """,
    example = """
        {
            "status": "ACTIVE",
            "reason": "Starting exam early because all students are ready"
        }
        """
)
public class ChangeExamStatusRequest {

    @Schema(
        description = """
            New status for the exam instance. Valid transitions depend on current status:

            **Status Definitions:**
            - **DRAFT**: Exam is being prepared, not yet scheduled
            - **SCHEDULED**: Exam is scheduled for future start time
            - **ACTIVE**: Exam is live, students can access and submit
            - **PAUSED**: Exam is temporarily suspended
            - **COMPLETED**: Exam has ended, results available
            - **CANCELLED**: Exam has been cancelled permanently

            **Valid Transitions:**
            - From DRAFT: → SCHEDULED, ACTIVE, CANCELLED
            - From SCHEDULED: → DRAFT, ACTIVE, CANCELLED
            - From ACTIVE: → PAUSED, COMPLETED
            - From PAUSED: → ACTIVE, COMPLETED, CANCELLED
            - COMPLETED and CANCELLED are final states
            """,
        example = "ACTIVE",
        allowableValues = {"DRAFT", "SCHEDULED", "ACTIVE", "PAUSED", "COMPLETED", "CANCELLED"},
        required = true,
        implementation = ExamInstanceStatus.class
    )
    @NotNull(message = "Trạng thái là bắt buộc")
    private ExamInstanceStatus status;

    @Schema(
        description = """
            Optional reason for the status change. This is stored for audit trail purposes
            and helps track why status changes were made.

            **Recommended for:**
            - Early starts: "All students ready, starting early"
            - Pauses: "Technical issues, pausing temporarily"
            - Early completion: "All students finished"
            - Cancellations: "Emergency situation, cancelling exam"

            **Best Practices:**
            - Keep it concise but descriptive
            - Include the main reason for the change
            - Avoid sensitive information
            - Use professional language
            """,
        example = "Starting exam early because all students are ready and the previous exam finished ahead of schedule",
        maxLength = 500
    )
    private String reason;
}
