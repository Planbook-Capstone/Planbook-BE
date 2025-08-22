package com.BE.service.job;

import com.BE.enums.ExamInstanceStatus;
import com.BE.model.entity.ExamInstance;
import com.BE.repository.ExamInstanceRepository;
import com.BE.service.implementService.ExamQuartzSchedulerService;
import com.BE.utils.DateNowUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Slf4j
public class ExamEndJob implements Job {
    
    @Autowired
    private ExamInstanceRepository examInstanceRepository;
    
    @Autowired
    private DateNowUtils dateNowUtils;
    
    @Autowired
    private ExamQuartzSchedulerService examQuartzSchedulerService;
    
    @Override
    @Transactional
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String examInstanceIdStr = jobExecutionContext.getJobDetail().getJobDataMap().getString("examInstanceId");
        UUID examInstanceId = UUID.fromString(examInstanceIdStr);

        try {
            ExamInstance instance = examInstanceRepository.findById(examInstanceId)
                    .orElseThrow(() -> new JobExecutionException("Exam instance not found for ID: " + examInstanceId));

            if (instance.getStatus() == ExamInstanceStatus.ACTIVE) {
                // Chuyển trạng thái sang COMPLETED
                ExamInstanceStatus oldStatus = instance.getStatus();
                instance.setStatus(ExamInstanceStatus.COMPLETED);
                instance.setStatusChangedAt(dateNowUtils.getCurrentDateTimeHCM());
                instance.setStatusChangeReason("Tự động kết thúc theo lịch");
                
                // Handle status change logic
                ExamInstanceStatus.COMPLETED.handleStatusChange(instance, oldStatus, dateNowUtils);
                
                examInstanceRepository.save(instance);

                log.info("Auto-ended exam {} at {}", examInstanceId, dateNowUtils.getCurrentDateTimeHCM());
                
                // Cleanup resources sau khi exam hoàn thành
                examQuartzSchedulerService.cleanupCompletedExam(examInstanceId.toString());
            } else {
                log.warn("Exam {} is not in ACTIVE status, current status: {}", examInstanceId, instance.getStatus());
            }
        } catch (Exception e) {
            log.error("Failed to auto-end exam {}: {}", examInstanceId, e.getMessage(), e);
            throw new JobExecutionException("Failed to auto-end exam: " + examInstanceId, e);
        }
    }
}
