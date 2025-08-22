package com.BE.service.interfaceService;

import com.BE.model.request.ChangeExamStatusRequest;
import com.BE.model.request.CreateExamInstanceRequest;
import com.BE.model.request.SubmitExamRequest;
import com.BE.model.request.UpdateExamInstanceRequest;
import com.BE.model.response.*;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IExamInstanceService {
    
    /**
     * Create a new exam instance from template
     */
    ExamInstanceResponse createExamInstance(CreateExamInstanceRequest request);

    /**
     * Get all exam instances by teacher
     */
    List<ExamInstanceResponse> getExamInstancesByTeacher();

    /**
     * Get exam instance by ID
     */
    ExamInstanceResponse getExamInstanceById(UUID instanceId);

    /**
     * Update exam instance
     */
    ExamInstanceResponse updateExamInstance(UUID instanceId, UpdateExamInstanceRequest request);

    /**
     * Delete exam instance
     */
    void deleteExamInstance(UUID instanceId);
    
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
    List<ExamSubmissionResponse> getExamSubmissions(UUID instanceId);

    /**
     * Generate Excel report for exam instance
     */
    Resource generateExcelReport(UUID instanceId);

    /**
     * Change exam instance status (start, pause, complete, cancel)
     */
    ExamInstanceResponse changeExamStatus(UUID instanceId, ChangeExamStatusRequest request);

    /**
     * Get valid status transitions for an exam instance
     */
    Map<String, Object> getValidStatusTransitions(UUID instanceId);

    /**
     * Get student submission result by submission ID (for students to view their results)
     */
    StudentSubmissionResultResponse getStudentSubmissionResult(UUID submissionId);
}
