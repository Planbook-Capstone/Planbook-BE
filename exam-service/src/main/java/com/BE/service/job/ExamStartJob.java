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
public class ExamStartJob implements Job {
    
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

            if (instance.getStatus() == ExamInstanceStatus.SCHEDULED) {
                // Chuyển trạng thái sang ACTIVE
                ExamInstanceStatus oldStatus = instance.getStatus();
                instance.setStatus(ExamInstanceStatus.ACTIVE);
                instance.setStatusChangedAt(dateNowUtils.getCurrentDateTimeHCM());
                instance.setStatusChangeReason("Tự động bắt đầu theo lịch");
                
                // Handle status change logic
                ExamInstanceStatus.ACTIVE.handleStatusChange(instance, oldStatus, dateNowUtils);
                
                examInstanceRepository.save(instance);

                log.info("Auto-started exam {} at {}", examInstanceId, dateNowUtils.getCurrentDateTimeHCM());
                
                // Lên lịch tự động kết thúc exam (sau khi save thành công)
                try {
                    examQuartzSchedulerService.scheduleExamEnd(instance);
                } catch (Exception e) {
                    log.error("Failed to schedule exam end for {}: {}", examInstanceId, e.getMessage(), e);
                }
            } else {
                log.warn("Exam {} is not in SCHEDULED status, current status: {}", examInstanceId, instance.getStatus());
            }
        } catch (Exception e) {
            log.error("Failed to auto-start exam {}: {}", examInstanceId, e.getMessage(), e);
            throw new JobExecutionException("Failed to auto-start exam: " + examInstanceId, e);
        }
    }
}
