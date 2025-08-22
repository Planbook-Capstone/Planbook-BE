package com.BE.service.implementService;

import com.BE.enums.ExamInstanceStatus;
import com.BE.model.entity.ExamInstance;
import com.BE.service.job.ExamEndJob;
import com.BE.service.job.ExamStartJob;
import com.BE.utils.DateNowUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@Slf4j
public class ExamQuartzSchedulerService {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private DateNowUtils dateNowUtils;

    @PostConstruct
    public void init() {
        try {
            if (scheduler != null) {
                log.info("🎯 Quartz Scheduler initialized: {} (Started: {})",
                        scheduler.getSchedulerName(), scheduler.isStarted());

                if (!scheduler.isStarted()) {
                    scheduler.start();
                    log.info("✅ Quartz Scheduler started successfully");
                }
            } else {
                log.error("❌ Quartz Scheduler is null!");
            }
        } catch (SchedulerException e) {
            log.error("❌ Failed to initialize Quartz Scheduler: {}", e.getMessage(), e);
        }
    }

    /**
     * Lên lịch tự động bắt đầu bài kiểm tra
     */
    public void scheduleExamStart(ExamInstance examInstance) {
        if (examInstance.getStatus() != ExamInstanceStatus.SCHEDULED) {
            log.debug("Exam {} is not in SCHEDULED status, current status: {}",
                    examInstance.getId(), examInstance.getStatus());
            return;
        }

        LocalDateTime startTime = examInstance.getStartAt();
        LocalDateTime now = dateNowUtils.getCurrentDateTimeHCM();

        // Chỉ lên lịch nếu thời gian bắt đầu ở tương lai
        if (startTime.isAfter(now)) {
            try {
                if (scheduler == null) {
                    log.error("❌ Scheduler is null! Cannot schedule exam start for {}", examInstance.getId());
                    return;
                }

                String examId = examInstance.getId().toString();

                // Hủy job cũ nếu có (để update)
                cancelStartJob(examId);

                // Tạo JobDetail cho ExamStartJob
                JobDetail jobDetail = JobBuilder.newJob(ExamStartJob.class)
                        .withIdentity(getStartJobKey(examId), "exams")
                        .usingJobData("examInstanceId", examId)
                        .build();

                // Tạo Trigger với múi giờ hệ thống
                Date triggerTime = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(getStartTriggerKey(examId), "exams")
                        .startAt(triggerTime)
                        .build();

                // Lên lịch job
                scheduler.scheduleJob(jobDetail, trigger);

                log.info("✅ Scheduled auto-start for exam {} at {}", examId, startTime);
            } catch (SchedulerException e) {
                log.error("❌ Error scheduling exam start for {}: {}", examInstance.getId(), e.getMessage(), e);
            }
        } else {
            log.warn("⚠️ Cannot schedule exam {} - start time {} is in the past", examInstance.getId(), startTime);
        }
    }

    /**
     * Lên lịch tự động kết thúc bài kiểm tra
     */
    public void scheduleExamEnd(ExamInstance examInstance) {
        if (examInstance.getStatus() != ExamInstanceStatus.ACTIVE) {
            log.debug("Exam {} is not in ACTIVE status, current status: {}",
                    examInstance.getId(), examInstance.getStatus());
            return;
        }

        LocalDateTime endTime = examInstance.getEndAt();
        LocalDateTime now = dateNowUtils.getCurrentDateTimeHCM();

        // Chỉ lên lịch nếu thời gian kết thúc ở tương lai
        if (endTime.isAfter(now)) {
            try {
                if (scheduler == null) {
                    log.error("❌ Scheduler is null! Cannot schedule exam end for {}", examInstance.getId());
                    return;
                }

                String examId = examInstance.getId().toString();

                // Hủy job cũ nếu có (để update)
                cancelEndJob(examId);

                // Tạo JobDetail cho ExamEndJob
                JobDetail jobDetail = JobBuilder.newJob(ExamEndJob.class)
                        .withIdentity(getEndJobKey(examId), "exams")
                        .usingJobData("examInstanceId", examId)
                        .build();

                // Tạo Trigger với múi giờ hệ thống
                Date triggerTime = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());
                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(getEndTriggerKey(examId), "exams")
                        .startAt(triggerTime)
                        .build();

                // Lên lịch job
                scheduler.scheduleJob(jobDetail, trigger);

                log.info("✅ Scheduled auto-end for exam {} at {}", examId, endTime);
            } catch (SchedulerException e) {
                log.error("❌ Error scheduling exam end for {}: {}", examInstance.getId(), e.getMessage(), e);
            }
        } else {
            log.warn("⚠️ Cannot schedule exam end {} - end time {} is in the past", examInstance.getId(), endTime);
        }
    }

    /**
     * Update lại schedule khi exam instance được cập nhật
     */
    public void updateExamSchedule(ExamInstance examInstance) {
        String examId = examInstance.getId().toString();

        // Hủy tất cả jobs cũ
        cancelExamSchedules(examId);

        // Tạo lại schedule dựa trên status hiện tại
        switch (examInstance.getStatus()) {
            case SCHEDULED:
                scheduleExamStart(examInstance);
                break;
            case ACTIVE:
                scheduleExamEnd(examInstance);
                break;
            default:
                // Không cần schedule cho các status khác
                break;
        }

        log.info("Updated schedule for exam {} with status {}", examId, examInstance.getStatus());
    }

    /**
     * Hủy các scheduled jobs cho một exam instance
     */
    public void cancelExamSchedules(String examInstanceId) {
        cancelStartJob(examInstanceId);
        cancelEndJob(examInstanceId);
    }

    /**
     * Cleanup và giải phóng bộ nhớ khi exam hoàn thành
     */
    public void cleanupCompletedExam(String examInstanceId) {
        cancelExamSchedules(examInstanceId);
        log.info("Cleaned up resources for completed exam: {}", examInstanceId);
    }

    /**
     * Hủy job start cho một exam
     */
    private void cancelStartJob(String examInstanceId) {
        try {
            JobKey startJobKey = JobKey.jobKey(getStartJobKey(examInstanceId), "exams");
            if (scheduler.checkExists(startJobKey)) {
                scheduler.deleteJob(startJobKey);
                log.debug("Cancelled start job for exam: {}", examInstanceId);
            }
        } catch (SchedulerException e) {
            log.error("Error cancelling start job for exam {}: {}", examInstanceId, e.getMessage(), e);
        }
    }

    /**
     * Hủy job end cho một exam
     */
    private void cancelEndJob(String examInstanceId) {
        try {
            JobKey endJobKey = JobKey.jobKey(getEndJobKey(examInstanceId), "exams");
            if (scheduler.checkExists(endJobKey)) {
                scheduler.deleteJob(endJobKey);
                log.debug("Cancelled end job for exam: {}", examInstanceId);
            }
        } catch (SchedulerException e) {
            log.error("Error cancelling end job for exam {}: {}", examInstanceId, e.getMessage(), e);
        }
    }

    // ===== Helper Methods =====

    private String getStartJobKey(String examInstanceId) {
        return "examStartJob_" + examInstanceId;
    }

    private String getStartTriggerKey(String examInstanceId) {
        return "startTrigger_" + examInstanceId;
    }

    private String getEndJobKey(String examInstanceId) {
        return "examEndJob_" + examInstanceId;
    }

    private String getEndTriggerKey(String examInstanceId) {
        return "endTrigger_" + examInstanceId;
    }
}
