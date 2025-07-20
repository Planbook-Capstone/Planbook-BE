package com.BE.service.interfaceService;

import com.BE.model.request.ChangeExamStatusRequest;
import com.BE.model.request.CreateExamInstanceRequest;
import com.BE.model.request.SubmitExamRequest;
import com.BE.model.request.UpdateExamInstanceRequest;
import com.BE.model.response.*;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.UUID;

public interface IExamInstanceService {
    
    /**
     * Create a new exam instance from template
     */
    ExamInstanceResponse createExamInstance(CreateExamInstanceRequest request, UUID teacherId);
    
    /**
     * Get all exam instances by teacher
     */
    List<ExamInstanceResponse> getExamInstancesByTeacher(UUID teacherId);
    
    /**
     * Get exam instance by ID
     */
    ExamInstanceResponse getExamInstanceById(UUID instanceId, UUID teacherId);
    
    /**
     * Update exam instance
     */
    ExamInstanceResponse updateExamInstance(UUID instanceId, UpdateExamInstanceRequest request, UUID teacherId);
    
    /**
     * Delete exam instance
     */
    void deleteExamInstance(UUID instanceId, UUID teacherId);
    
    /**
     * Get exam content by code (for students)
     */
    ExamContentResponse getExamByCode(String code);
    
    /**
     * Submit exam answers (for students)
     */
    SubmitExamResponse submitExam(String code, SubmitExamRequest request);
    
    /**
     * Get exam submissions for an instance
     */
    List<ExamSubmissionResponse> getExamSubmissions(UUID instanceId, UUID teacherId);
    
    /**
     * Generate Excel report for exam instance
     */
    Resource generateExcelReport(UUID instanceId, UUID teacherId);

    /**
     * Change exam instance status (start, pause, complete, cancel)
     */
    ExamInstanceResponse changeExamStatus(UUID instanceId, ChangeExamStatusRequest request, UUID teacherId);
}
