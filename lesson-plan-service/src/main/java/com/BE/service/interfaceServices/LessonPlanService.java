package com.BE.service.interfaceServices;

import com.BE.enums.Status;
import com.BE.model.response.LessonPlanDTO;
import com.BE.model.request.CreateLessonPlanRequest;
import com.BE.model.request.UpdateLessonPlanRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for LessonPlan operations
 */
public interface LessonPlanService {

    /**
     * Create a new lesson plan
     */
    LessonPlanDTO createLessonPlan(CreateLessonPlanRequest request);

    /**
     * Get lesson plan by ID
     */
    LessonPlanDTO getLessonPlanById(Long id);

    /**
     * Update lesson plan
     */
    LessonPlanDTO updateLessonPlan(Long id, UpdateLessonPlanRequest request);

    /**
     * Soft delete lesson plan (set status to INACTIVE)
     */
    void deleteLessonPlan(Long id);

    /**
     * Get all lesson plans with pagination, search and status filter
     */
    Page<LessonPlanDTO> getAllLessonPlans(String keyword, Status status, Pageable pageable);
}
