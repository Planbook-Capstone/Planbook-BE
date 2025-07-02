package com.BE.service.implementServices;

import com.BE.enums.Status;
import com.BE.mapper.LessonPlanMapper;
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
    private final LessonPlanMapper lessonPlanMapper;

    @Override
    public LessonPlanDTO createLessonPlan(CreateLessonPlanRequest request) {
        log.info("Creating new lesson plan with name: {}", request.getName());

        // Convert request to entity using mapper
        LessonPlan lessonPlan = lessonPlanMapper.toEntity(request);

        LessonPlan savedLessonPlan = lessonPlanRepository.save(lessonPlan);
        log.info("Created lesson plan with ID: {}", savedLessonPlan.getId());

        return lessonPlanMapper.toDTO(savedLessonPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public LessonPlanDTO getLessonPlanById(Long id) {
        log.info("Getting lesson plan by ID: {}", id);

        LessonPlan lessonPlan = lessonPlanRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo án với ID: " + id));

        return lessonPlanMapper.toDTO(lessonPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LessonPlanDTO> getAllLessonPlans(String keyword, Status status, Pageable pageable) {
        log.info("Getting all lesson plans with keyword: '{}', status: {}, page: {}", keyword, status, pageable.getPageNumber());

        Page<LessonPlan> lessonPlans = lessonPlanRepository.findWithSearchAndStatus(keyword, status, pageable);

        return lessonPlans.map(lessonPlanMapper::toDTO);
    }

    @Override
    public LessonPlanDTO updateLessonPlan(Long id, UpdateLessonPlanRequest request) {
        log.info("Updating lesson plan with ID: {}", id);

        LessonPlan lessonPlan = lessonPlanRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo án với ID: " + id));

        // Update fields using mapper
        lessonPlanMapper.updateEntityFromRequest(lessonPlan, request);

        LessonPlan savedLessonPlan = lessonPlanRepository.save(lessonPlan);
        log.info("Updated lesson plan with ID: {}", savedLessonPlan.getId());

        return lessonPlanMapper.toDTO(savedLessonPlan);
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
}
