package com.BE.service.interfaceService;
import com.BE.model.request.CreateExamTemplateRequest;
import com.BE.model.request.UpdateExamTemplateRequest;
import com.BE.model.response.ExamTemplateResponse;
import java.util.List;
import java.util.UUID;

public interface IExamTemplateService {
    
    /**
     * Create a new exam template
     */
    ExamTemplateResponse createExamTemplate(CreateExamTemplateRequest request, UUID teacherId);
    
    /**
     * Get all exam templates by teacher
     */
    List<ExamTemplateResponse> getExamTemplatesByTeacher(UUID teacherId);
    
    /**
     * Get exam template by ID
     */
    ExamTemplateResponse getExamTemplateById(UUID templateId, UUID teacherId);
    
    /**
     * Update exam template
     */
    ExamTemplateResponse updateExamTemplate(UUID templateId, UpdateExamTemplateRequest request, UUID teacherId);
    
    /**
     * Delete exam template
     */
    void deleteExamTemplate(UUID templateId, UUID teacherId);

    /**
     * Clone exam template
     */
    ExamTemplateResponse cloneExamTemplate(UUID templateId, UUID teacherId);
}
