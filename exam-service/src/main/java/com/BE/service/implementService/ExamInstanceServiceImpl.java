package com.BE.service.implementService;

import com.BE.enums.ExamInstanceStatus;
import com.BE.mapper.ExamInstanceMapper;
import com.BE.mapper.ExamResultDetailMapper;
import com.BE.mapper.ExamSubmissionMapper;
import com.BE.model.request.ChangeExamStatusRequest;
import com.BE.model.request.CreateExamInstanceRequest;
import com.BE.model.request.SubmitExamRequest;
import com.BE.model.request.UpdateExamInstanceRequest;
import com.BE.model.response.*;
import com.BE.model.entity.ExamInstance;
import com.BE.model.entity.ExamResultDetail;
import com.BE.model.entity.ExamSubmission;
import com.BE.model.entity.ExamTemplate;
import com.BE.exception.BadRequestException;
import com.BE.exception.ResourceNotFoundException;
import com.BE.repository.ExamInstanceRepository;
import com.BE.repository.ExamResultDetailRepository;
import com.BE.repository.ExamSubmissionRepository;
import com.BE.repository.ExamTemplateRepository;
import com.BE.service.interfaceService.IExamInstanceService;
import com.BE.service.interfaceService.IExcelService;
import com.BE.utils.DateNowUtils;
import com.BE.utils.ExamGradingUtils;
import com.BE.utils.ExamUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExamInstanceServiceImpl implements IExamInstanceService {

    private final ExamInstanceRepository examInstanceRepository;
    private final ExamTemplateRepository examTemplateRepository;
    private final ExamSubmissionRepository examSubmissionRepository;
    private final ExamResultDetailRepository examResultDetailRepository;
    private final IExcelService excelService;
    private final ObjectMapper objectMapper;
    private final DateNowUtils dateNowUtils;
    private final ExamUtils examUtils;
    private final ExamGradingUtils examGradingUtils;
    private final ExamInstanceMapper examInstanceMapper;
    private final ExamSubmissionMapper examSubmissionMapper;
    private final ExamResultDetailMapper examResultDetailMapper;

    @Override
    public ExamInstanceResponse createExamInstance(CreateExamInstanceRequest request, UUID teacherId) {
        // Validate template ownership
        ExamTemplate template = examTemplateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new ResourceNotFoundException("Exam template not found"));
        
        if (!template.getCreatedBy().equals(teacherId)) {
            throw new BadRequestException("Access denied to this exam template");
        }
        
        // Validate time range
        if (request.getEndAt().isBefore(request.getStartAt())) {
            throw new BadRequestException("End time must be after start time");
        }
        
        ExamInstance instance = examInstanceMapper.toEntity(request, template);
        instance.setCode(examUtils.generateUniqueCode());

        ExamInstance savedInstance = examInstanceRepository.save(instance);
        return examInstanceMapper.toResponse(savedInstance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamInstanceResponse> getExamInstancesByTeacher(UUID teacherId) {
        List<ExamInstance> instances = examInstanceRepository.findByTeacherIdOrderByCreatedAtDesc(teacherId);
        return instances.stream()
                .map(examInstanceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ExamInstanceResponse getExamInstanceById(UUID instanceId, UUID teacherId) {
        ExamInstance instance = examInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam instance not found"));

        if (!instance.getTemplate().getCreatedBy().equals(teacherId)) {
            throw new BadRequestException("Access denied to this exam instance");
        }

        return examInstanceMapper.toResponse(instance);
    }

    @Override
    public ExamInstanceResponse updateExamInstance(UUID instanceId, UpdateExamInstanceRequest request, UUID teacherId) {
        ExamInstance instance = examInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam instance not found"));

        if (!instance.getTemplate().getCreatedBy().equals(teacherId)) {
            throw new BadRequestException("Access denied to this exam instance");
        }

        // Use MapStruct to update entity
        examInstanceMapper.updateEntity(instance, request);

        // Validate time range if both times are set
        if (instance.getEndAt().isBefore(instance.getStartAt())) {
            throw new BadRequestException("End time must be after start time");
        }

        ExamInstance savedInstance = examInstanceRepository.save(instance);
        return examInstanceMapper.toResponse(savedInstance);
    }

    @Override
    public void deleteExamInstance(UUID instanceId, UUID teacherId) {
        ExamInstance instance = examInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam instance not found"));

        if (!instance.getTemplate().getCreatedBy().equals(teacherId)) {
            throw new BadRequestException("Access denied to this exam instance");
        }

        // Delete related submissions and result details first
        List<ExamSubmission> submissions = examSubmissionRepository.findByExamInstanceIdOrderBySubmittedAtDesc(instanceId);
        for (ExamSubmission submission : submissions) {
            examResultDetailRepository.deleteBySubmissionId(submission.getId());
        }
        examSubmissionRepository.deleteByExamInstanceId(instanceId);

        examInstanceRepository.delete(instance);
        log.info("Deleted exam instance {} by teacher {}", instanceId, teacherId);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamContentResponse getExamByCode(String code) {
        ExamInstance instance = examInstanceRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        // Check if exam is accessible to students
        if (!instance.getStatus().isAccessible()) {
            throw new BadRequestException(
                String.format("Exam is not available. Current status: %s (%s)",
                    instance.getStatus().getCode(), instance.getStatus().getDescription())
            );
        }

        // Additional time-based check for ACTIVE status
        LocalDateTime now = dateNowUtils.getCurrentDateTimeHCM();
        if (instance.getStatus() == ExamInstanceStatus.ACTIVE) {
            if (now.isBefore(instance.getStartAt()) || now.isAfter(instance.getEndAt())) {
                throw new BadRequestException("Exam is not available at this time");
            }
        }

        // Remove correct answers from content for students
        Map<String, Object> studentContent = examUtils.removeCorrectAnswers(instance.getTemplate().getContentJson());

        return examInstanceMapper.toContentResponse(instance, studentContent);
    }

    @Override
    public SubmitExamResponse submitExam(String code, SubmitExamRequest request) {
        ExamInstance instance = examInstanceRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        // Check if exam allows submissions
        if (!instance.getStatus().isSubmittable()) {
            throw new BadRequestException(
                String.format("Exam submissions are not allowed. Current status: %s (%s)",
                    instance.getStatus().getCode(), instance.getStatus().getDescription())
            );
        }

        // Additional time-based check for ACTIVE status
        LocalDateTime now = dateNowUtils.getCurrentDateTimeHCM();
        if (instance.getStatus() == ExamInstanceStatus.ACTIVE) {
            if (now.isBefore(instance.getStartAt()) || now.isAfter(instance.getEndAt())) {
                throw new BadRequestException("Exam submission is not available at this time");
            }
        }

        

        // Grade the exam using new flat answer format
        ExamGradingResult gradingResult = examGradingUtils.gradeExamWithFlatAnswersAndCustomConfig(
            instance.getTemplate().getContentJson(),
            request.getAnswers(),
            instance.getTemplate().getGradingConfig(),
            instance.getTemplate().getTotalScore()
        );

        // Convert answers list to JSON for storage
        Map<String, Object> answersJson;
        try {
            // Log the original answers for debugging
            log.info("Original student answers: {}", request.getAnswers());

            // Store the answers directly as a Map with "answers" key
                answersJson = new HashMap<>();
            answersJson.put("answers", request.getAnswers());

            // Log the converted JSON for debugging
            log.info("Converted answers JSON: {}", objectMapper.writeValueAsString(answersJson));
        } catch (Exception e) {
            log.error("Error converting answers to JSON: {}", e.getMessage(), e);
            // Still store the original answers even if logging fails
            answersJson = new HashMap<>();
            answersJson.put("answers", request.getAnswers());
        }

        // Save submission using MapStruct
        ExamSubmission submission = examSubmissionMapper.toEntity(request, instance, gradingResult, answersJson);
        ExamSubmission savedSubmission = examSubmissionRepository.save(submission);

        // Save detailed results
        saveDetailedResults(savedSubmission, gradingResult.getDetails());

        // Generate/update Excel file
        try {
            excelService.generateExcelReport(instance.getId());
        } catch (Exception e) {
            log.error("Error generating Excel report: {}", e.getMessage());
        }

        return examSubmissionMapper.toSubmitResponse(savedSubmission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamSubmissionResponse> getExamSubmissions(UUID instanceId, UUID teacherId) {
        ExamInstance instance = examInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam instance not found"));

        if (!instance.getTemplate().getCreatedBy().equals(teacherId)) {
            throw new BadRequestException("Access denied to this exam instance");
        }

        List<ExamSubmission> submissions = examSubmissionRepository.findByExamInstanceIdOrderBySubmittedAtDesc(instanceId);
        return submissions.stream()
                .map(examSubmissionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Resource generateExcelReport(UUID instanceId, UUID teacherId) {
        ExamInstance instance = examInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam instance not found"));

        if (!instance.getTemplate().getCreatedBy().equals(teacherId)) {
            throw new BadRequestException("Access denied to this exam instance");
        }

        return excelService.generateExcelReport(instanceId);
    }

    @Override
    public ExamInstanceResponse changeExamStatus(UUID instanceId, ChangeExamStatusRequest request, UUID teacherId) {
        ExamInstance instance = examInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam instance not found"));

        if (!instance.getTemplate().getCreatedBy().equals(teacherId)) {
            throw new BadRequestException("Access denied to this exam instance");
        }

        ExamInstanceStatus currentStatus = instance.getStatus();
        ExamInstanceStatus newStatus = request.getStatus();

        // Validate status transition using enum method
        newStatus.validateTransitionFrom(currentStatus, instance, dateNowUtils);

        // Update status
        instance.setStatus(newStatus);
        instance.setStatusChangedAt(dateNowUtils.getCurrentDateTimeHCM());
        instance.setStatusChangeReason(request.getReason());

        // Handle special status changes using enum method
        newStatus.handleStatusChange(instance, currentStatus, dateNowUtils);

        ExamInstance savedInstance = examInstanceRepository.save(instance);
        log.info("Changed exam instance {} status from {} to {} by teacher {}",
                instanceId, currentStatus, newStatus, teacherId);

        return examInstanceMapper.toResponse(savedInstance);
    }



    /**
     * Save detailed exam results
     */
    private void saveDetailedResults(ExamSubmission submission, List<ExamResultDetailData> details) {
        List<ExamResultDetail> resultDetails = examResultDetailMapper.toEntityList(details, submission);
        examResultDetailRepository.saveAll(resultDetails);
    }


}
