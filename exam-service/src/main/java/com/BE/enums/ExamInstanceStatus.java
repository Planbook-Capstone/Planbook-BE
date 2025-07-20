package com.BE.enums;

import com.BE.exception.BadRequestException;
import com.BE.model.entity.ExamInstance;
import com.BE.utils.DateNowUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Getter
@Slf4j
@Schema(
    description = """
        Exam instance status enum representing the current state of an exam.
        Controls student access and defines valid operations.
        """,
    example = "ACTIVE"
)
public enum ExamInstanceStatus {
    @Schema(description = "Exam is in draft state - being prepared, students cannot access")
    DRAFT("DRAFT", "Bản nháp - chưa được kích hoạt"),

    @Schema(description = "Exam is scheduled for future start - students cannot access yet")
    SCHEDULED("SCHEDULED", "Đã lên lịch - chờ đến giờ bắt đầu"),

    @Schema(description = "Exam is active and running - students can access and submit")
    ACTIVE("ACTIVE", "Đang diễn ra - học sinh có thể làm bài"),

    @Schema(description = "Exam is temporarily paused - students cannot access")
    PAUSED("PAUSED", "Tạm dừng - không thể làm bài"),

    @Schema(description = "Exam has completed - students cannot submit, results available")
    COMPLETED("COMPLETED", "Đã kết thúc - không thể làm bài"),

    @Schema(description = "Exam has been cancelled - students cannot access, no results")
    CANCELLED("CANCELLED", "Đã hủy - không thể làm bài");

    private final String code;
    private final String description;

    ExamInstanceStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Check if students can access exam in this status
     */
    public boolean isAccessible() {
        return this == ACTIVE;
    }

    /**
     * Check if students can submit exam in this status
     */
    public boolean isSubmittable() {
        return this == ACTIVE;
    }

    /**
     * Check if teacher can modify exam in this status
     */
    public boolean isModifiable() {
        return this == DRAFT || this == SCHEDULED;
    }

    /**
     * Check if exam can be started manually
     */
    public boolean canStart() {
        return this == DRAFT || this == SCHEDULED || this == PAUSED;
    }

    /**
     * Check if exam can be paused
     */
    public boolean canPause() {
        return this == ACTIVE;
    }

    /**
     * Check if exam can be completed manually
     */
    public boolean canComplete() {
        return this == ACTIVE || this == PAUSED;
    }

    /**
     * Check if exam can be cancelled
     */
    public boolean canCancel() {
        return this == DRAFT || this == SCHEDULED || this == PAUSED;
    }

    /**
     * Validate status transition from current status to this status
     */
    public void validateTransitionFrom(ExamInstanceStatus currentStatus, ExamInstance instance, DateNowUtils dateNowUtils) {
        LocalDateTime now = dateNowUtils.getCurrentDateTimeHCM();

        switch (this) {
            case ACTIVE:
                if (!currentStatus.canStart()) {
                    throw new BadRequestException(
                        String.format("Cannot start exam from status %s. Current status must be DRAFT, SCHEDULED, or PAUSED.", currentStatus)
                    );
                }
                break;

            case PAUSED:
                if (!currentStatus.canPause()) {
                    throw new BadRequestException(
                        String.format("Cannot pause exam from status %s. Current status must be ACTIVE.", currentStatus)
                    );
                }
                break;

            case COMPLETED:
                if (!currentStatus.canComplete()) {
                    throw new BadRequestException(
                        String.format("Cannot complete exam from status %s. Current status must be ACTIVE or PAUSED.", currentStatus)
                    );
                }
                break;

            case CANCELLED:
                if (!currentStatus.canCancel()) {
                    throw new BadRequestException(
                        String.format("Cannot cancel exam from status %s. Current status must be DRAFT, SCHEDULED, or PAUSED.", currentStatus)
                    );
                }
                break;

            case SCHEDULED:
                if (currentStatus != DRAFT) {
                    throw new BadRequestException("Can only schedule exam from DRAFT status");
                }
                if (instance.getStartAt().isBefore(now)) {
                    throw new BadRequestException("Cannot schedule exam with start time in the past");
                }
                break;

            case DRAFT:
                if (currentStatus != SCHEDULED) {
                    throw new BadRequestException("Can only move to DRAFT from SCHEDULED status");
                }
                break;

            default:
                throw new BadRequestException("Invalid status: " + this);
        }
    }

    /**
     * Handle special logic when transitioning to this status
     */
    public void handleStatusChange(ExamInstance instance, ExamInstanceStatus oldStatus, DateNowUtils dateNowUtils) {
        switch (this) {
            case ACTIVE:
                // When starting exam, always update start time to current time
                LocalDateTime now = dateNowUtils.getCurrentDateTimeHCM();
                LocalDateTime originalStartTime = instance.getStartAt();
                instance.setStartAt(now);
                log.info("Starting exam {}. Original start time: {}, Actual start time: {}",
                        instance.getId(), originalStartTime, now);
                break;

            case COMPLETED:
                // When completing exam, update end time if ending early
                LocalDateTime currentTime = dateNowUtils.getCurrentDateTimeHCM();
                if (currentTime.isBefore(instance.getEndAt())) {
                    log.info("Completing exam {} early. Original end time: {}, Actual end time: {}",
                            instance.getId(), instance.getEndAt(), currentTime);
                }
                break;

            case CANCELLED:
                // Log cancellation
                log.info("Exam {} cancelled. Reason: {}", instance.getId(), instance.getStatusChangeReason());
                break;

            default:
                // No special handling needed
                break;
        }
    }
}
