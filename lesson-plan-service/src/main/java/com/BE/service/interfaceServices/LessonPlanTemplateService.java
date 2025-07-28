package com.BE.service.interfaceServices;

import com.BE.enums.Status;
import com.BE.model.response.LessonPlanTemplateDTO;
import com.BE.model.request.CreateLessonPlanRequest;
import com.BE.model.request.UpdateLessonPlanRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for LessonPlan operations
 */
public interface LessonPlanTemplateService {

    /**
     * Create a new lesson plan
     */
    LessonPlanTemplateDTO createLessonPlan(CreateLessonPlanRequest request);

    /**
     * Get lesson plan by ID
     */
    LessonPlanTemplateDTO getLessonPlanById(Long id);

    /**
     * Update lesson plan
     */
    LessonPlanTemplateDTO updateLessonPlan(Long id, UpdateLessonPlanRequest request);

    /**
     * Soft delete lesson plan (set status to INACTIVE)
     */
    void deleteLessonPlan(Long id);

    /**
     * Get all lesson plans with pagination, search and status filter
     */
    Page<LessonPlanTemplateDTO> getAllLessonPlans(String keyword, Status status, Pageable pageable);
}
