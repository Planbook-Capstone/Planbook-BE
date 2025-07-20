package com.BE.service.implementService;

import com.BE.mapper.ExamTemplateMapper;
import com.BE.model.request.CreateExamTemplateRequest;
import com.BE.model.request.UpdateExamTemplateRequest;
import com.BE.model.response.ExamTemplateResponse;
import com.BE.model.entity.ExamTemplate;
import com.BE.exception.BadRequestException;
import com.BE.exception.ResourceNotFoundException;
import com.BE.repository.ExamTemplateRepository;
import com.BE.service.interfaceService.IExamTemplateService;
import com.BE.utils.ExamUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExamTemplateServiceImpl implements IExamTemplateService {

    private final ExamTemplateRepository examTemplateRepository;
    private final ExamUtils examUtils;
    private final ExamTemplateMapper examTemplateMapper;

    @Override
    public ExamTemplateResponse createExamTemplate(CreateExamTemplateRequest request, UUID teacherId) {
        try {
            // Validate content structure
//            examUtils.validateExamContent(request.getContentJson());

            ExamTemplate template = examTemplateMapper.toEntity(request, teacherId);
            ExamTemplate savedTemplate = examTemplateRepository.save(template);
            return examTemplateMapper.toResponse(savedTemplate);

        } catch (Exception e) {
            log.error("Error creating exam template: {}", e.getMessage());
            throw new BadRequestException("Invalid content format: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamTemplateResponse> getExamTemplatesByTeacher(UUID teacherId) {
        List<ExamTemplate> templates = examTemplateRepository.findByCreatedByOrderByCreatedAtDesc(teacherId);
        return templates.stream()
                .map(examTemplateMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ExamTemplateResponse getExamTemplateById(UUID templateId, UUID teacherId) {
        ExamTemplate template = examTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam template not found"));

        if (!template.getCreatedBy().equals(teacherId)) {
            throw new BadRequestException("Access denied to this exam template");
        }

        return examTemplateMapper.toResponse(template);
    }

    @Override
    public ExamTemplateResponse updateExamTemplate(UUID templateId, UpdateExamTemplateRequest request, UUID teacherId) {
        ExamTemplate template = examTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam template not found"));

        if (!template.getCreatedBy().equals(teacherId)) {
            throw new BadRequestException("Access denied to this exam template");
        }

        try {
            // Validate content structure if provided
            if (request.getContentJson() != null) {
                examUtils.validateExamContent(request.getContentJson());
            }

            // Use MapStruct to update entity
            examTemplateMapper.updateEntity(template, request);

            // Increment version
            template.setVersion(template.getVersion() + 1);

            ExamTemplate savedTemplate = examTemplateRepository.save(template);
            return examTemplateMapper.toResponse(savedTemplate);

        } catch (Exception e) {
            log.error("Error updating exam template: {}", e.getMessage());
            throw new BadRequestException("Invalid content format: " + e.getMessage());
        }
    }

    @Override
    public void deleteExamTemplate(UUID templateId, UUID teacherId) {
        ExamTemplate template = examTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam template not found"));

        if (!template.getCreatedBy().equals(teacherId)) {
            throw new BadRequestException("Access denied to this exam template");
        }

        // Check if template is being used by any instances
        // This would require checking ExamInstance repository
        // For now, we'll allow deletion but in production you might want to prevent this

        examTemplateRepository.delete(template);
        log.info("Deleted exam template {} by teacher {}", templateId, teacherId);
    }

    @Override
    public ExamTemplateResponse cloneExamTemplate(UUID templateId, UUID teacherId) {
        // Get the original template
        ExamTemplate originalTemplate = examTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam template not found"));

        if (!originalTemplate.getCreatedBy().equals(teacherId)) {
            throw new BadRequestException("Access denied to this exam template");
        }

        try {
            // Use MapStruct to clone template
            ExamTemplate clonedTemplate = examTemplateMapper.cloneEntity(originalTemplate, teacherId);
            ExamTemplate savedTemplate = examTemplateRepository.save(clonedTemplate);
            log.info("Cloned exam template {} to {} by teacher {}", templateId, savedTemplate.getId(), teacherId);

            return examTemplateMapper.toResponse(savedTemplate);

        } catch (Exception e) {
            log.error("Error cloning exam template: {}", e.getMessage());
            throw new BadRequestException("Error cloning template: " + e.getMessage());
        }
    }


}
