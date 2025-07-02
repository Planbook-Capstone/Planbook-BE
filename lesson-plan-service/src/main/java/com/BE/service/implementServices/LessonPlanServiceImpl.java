package com.BE.service.implementServices;

import com.BE.enums.Status;
import com.BE.model.dto.LessonPlanDTO;
import com.BE.model.entity.LessonPlan;
import com.BE.model.request.CreateLessonPlanRequest;
import com.BE.model.request.UpdateLessonPlanRequest;
import com.BE.repository.LessonPlanRepository;
import com.BE.service.interfaceServices.LessonPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of LessonPlanService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LessonPlanServiceImpl implements LessonPlanService {

    private final LessonPlanRepository lessonPlanRepository;

    @Override
    public LessonPlanDTO createLessonPlan(CreateLessonPlanRequest request) {
        log.info("Creating new lesson plan with name: {}", request.getName());

        LessonPlan lessonPlan = new LessonPlan();
        lessonPlan.setName(request.getName());
        lessonPlan.setDescription(request.getDescription());
        lessonPlan.setStatus(request.getStatus() != null ? request.getStatus() : Status.ACTIVE);

        LessonPlan savedLessonPlan = lessonPlanRepository.save(lessonPlan);
        log.info("Created lesson plan with ID: {}", savedLessonPlan.getId());

        return convertToDTO(savedLessonPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public LessonPlanDTO getLessonPlanById(Long id) {
        log.info("Getting lesson plan by ID: {}", id);

        LessonPlan lessonPlan = lessonPlanRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo án với ID: " + id));

        return convertToDTO(lessonPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LessonPlanDTO> getAllLessonPlans(String keyword, Status status, Pageable pageable) {
        log.info("Getting all lesson plans with keyword: '{}', status: {}, page: {}", keyword, status, pageable.getPageNumber());

        Page<LessonPlan> lessonPlans = lessonPlanRepository.findWithSearchAndStatus(keyword, status, pageable);
        
        return lessonPlans.map(this::convertToDTO);
    }

    @Override
    public LessonPlanDTO updateLessonPlan(Long id, UpdateLessonPlanRequest request) {
        log.info("Updating lesson plan with ID: {}", id);

        LessonPlan lessonPlan = lessonPlanRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo án với ID: " + id));
        
        // Update fields if provided
        if (request.getName() != null) {
            lessonPlan.setName(request.getName());
        }
        if (request.getDescription() != null) {
            lessonPlan.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            lessonPlan.setStatus(request.getStatus());
        }
        
        LessonPlan savedLessonPlan = lessonPlanRepository.save(lessonPlan);
        log.info("Updated lesson plan with ID: {}", savedLessonPlan.getId());

        return convertToDTO(savedLessonPlan);
    }

    @Override
    public void deleteLessonPlan(Long id) {
        log.info("Soft deleting lesson plan with ID: {}", id);

        LessonPlan lessonPlan = lessonPlanRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo án với ID: " + id));

        lessonPlan.setStatus(Status.INACTIVE);
        lessonPlanRepository.save(lessonPlan);
        
        log.info("Soft deleted lesson plan with ID: {}", id);
    }

    private LessonPlanDTO convertToDTO(LessonPlan lessonPlan) {
        LessonPlanDTO dto = new LessonPlanDTO();
        dto.setId(lessonPlan.getId());
        dto.setName(lessonPlan.getName());
        dto.setDescription(lessonPlan.getDescription());
        dto.setCreatedAt(lessonPlan.getCreatedAt());
        dto.setUpdatedAt(lessonPlan.getUpdatedAt());
        dto.setStatus(lessonPlan.getStatus());
        return dto;
    }
}
