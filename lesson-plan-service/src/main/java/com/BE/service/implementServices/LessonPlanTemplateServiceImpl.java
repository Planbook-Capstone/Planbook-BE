package com.BE.service.implementServices;

import com.BE.enums.Status;
import com.BE.mapper.LessonPlanTemplateMapper;
import com.BE.model.response.LessonPlanTemplateDTO;
import com.BE.model.entity.LessonPlanTemplate;
import com.BE.model.request.CreateLessonPlanRequest;
import com.BE.model.request.UpdateLessonPlanRequest;
import com.BE.repository.LessonPlanTemplateRepository;
import com.BE.service.interfaceServices.LessonPlanTemplateService;
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
public class LessonPlanTemplateServiceImpl implements LessonPlanTemplateService {

    private final LessonPlanTemplateRepository lessonPlanTemplateRepository;
    private final LessonPlanTemplateMapper lessonPlanTemplateMapper;

    @Override
    public LessonPlanTemplateDTO createLessonPlan(CreateLessonPlanRequest request) {
        log.info("Creating new lesson plan with name: {}", request.getName());

        // Convert request to entity using mapper
        LessonPlanTemplate lessonPlanTemplate = lessonPlanTemplateMapper.toEntity(request);

        LessonPlanTemplate savedLessonPlan = lessonPlanTemplateRepository.save(lessonPlanTemplate);
        log.info("Created lesson plan with ID: {}", savedLessonPlan.getId());

        return lessonPlanTemplateMapper.toDTO(savedLessonPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public LessonPlanTemplateDTO getLessonPlanById(Long id) {
        log.info("Getting lesson plan by ID: {}", id);

        LessonPlanTemplate lessonPlanTemplate = lessonPlanTemplateRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo án với ID: " + id));

        return lessonPlanTemplateMapper.toDTO(lessonPlanTemplate);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LessonPlanTemplateDTO> getAllLessonPlans(String keyword, Status status, Pageable pageable) {
        log.info("Getting all lesson plans with keyword: '{}', status: {}, page: {}", keyword, status, pageable.getPageNumber());

        Page<LessonPlanTemplate> lessonPlanTemplates = lessonPlanTemplateRepository.findWithSearchAndStatus(keyword, status, pageable);

        return lessonPlanTemplates.map(lessonPlanTemplateMapper::toDTO);
    }

    @Override
    public LessonPlanTemplateDTO updateLessonPlan(Long id, UpdateLessonPlanRequest request) {
        log.info("Updating lesson plan with ID: {}", id);

        LessonPlanTemplate lessonPlanTemplate = lessonPlanTemplateRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo án với ID: " + id));

        // Update fields using mapper
        lessonPlanTemplateMapper.updateEntityFromRequest(lessonPlanTemplate, request);

        LessonPlanTemplate savedLessonPlan = lessonPlanTemplateRepository.save(lessonPlanTemplate);
        log.info("Updated lesson plan with ID: {}", savedLessonPlan.getId());

        return lessonPlanTemplateMapper.toDTO(savedLessonPlan);
    }

    @Override
    public void deleteLessonPlan(Long id) {
        log.info("Soft deleting lesson plan with ID: {}", id);

        LessonPlanTemplate lessonPlanTemplate = lessonPlanTemplateRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo án với ID: " + id));

        lessonPlanTemplate.setStatus(Status.INACTIVE);
        lessonPlanTemplateRepository.save(lessonPlanTemplate);
        
        log.info("Soft deleted lesson plan with ID: {}", id);
    }
}
