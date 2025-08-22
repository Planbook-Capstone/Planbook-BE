package com.BE.service.implementService;

import com.BE.enums.ExamInstanceStatus;
import com.BE.mapper.ExamInstanceMapper;
import com.BE.mapper.ExamResultDetailMapper;
import com.BE.mapper.ExamSubmissionMapper;
import com.BE.mapper.StudentSubmissionResultMapper;
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
import com.BE.utils.AccountUtils;
import com.BE.utils.DateNowUtils;
import com.BE.utils.ExamGradingUtils;
import com.BE.utils.ExamUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

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
    private final StudentSubmissionResultMapper studentSubmissionResultMapper;
    private final AccountUtils accountUtils;
    private final ExamQuartzSchedulerService examQuartzSchedulerService;

    @Override
    public ExamInstanceResponse createExamInstance(CreateExamInstanceRequest request) {
        UUID teacherId = accountUtils.getCurrentUserId();
        // Validate template ownership
        ExamTemplate template = examTemplateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mẫu đề thi"));

        if (!template.getCreatedBy().equals(teacherId)) {
            throw new BadRequestException("Truy cập bị từ chối đối với mẫu đề thi này");
        }
        
        // Validate time range
        if (request.getEndAt().isBefore(request.getStartAt())) {
            throw new BadRequestException("Thời gian kết thúc phải sau thời gian bắt đầu");
        }
        
        ExamInstance instance = examInstanceMapper.toEntity(request, template);
        instance.setCode(examUtils.generateUniqueCode());

        ExamInstance savedInstance = examInstanceRepository.save(instance);

        log.info("📝 Created exam instance: ID={}, Status={}, StartAt={}",
                savedInstance.getId(), savedInstance.getStatus(), savedInstance.getStartAt());

        // Lên lịch tự động bắt đầu nếu status là SCHEDULED
        if (savedInstance.getStatus() == ExamInstanceStatus.SCHEDULED) {
            if (examQuartzSchedulerService != null) {
                examQuartzSchedulerService.scheduleExamStart(savedInstance);
            } else {
                log.error("❌ ExamQuartzSchedulerService is null!");
            }
        }

        return examInstanceMapper.toResponse(savedInstance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamInstanceResponse> getExamInstancesByTeacher() {
        UUID teacherId = accountUtils.getCurrentUserId();
        List<ExamInstance> instances = examInstanceRepository.findByTeacherIdOrderByCreatedAtDesc(teacherId);
        return instances.stream()
                .map(examInstanceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ExamInstanceResponse getExamInstanceById(UUID instanceId) {
        UUID teacherId = accountUtils.getCurrentUserId();
        ExamInstance instance = examInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiên thi"));

        if (!instance.getTemplate().getCreatedBy().equals(teacherId)) {
            throw new BadRequestException("Truy cập bị từ chối đối với phiên thi này");
        }

        return examInstanceMapper.toResponse(instance);
    }

    @Override
    public ExamInstanceResponse updateExamInstance(UUID instanceId, UpdateExamInstanceRequest request) {
        UUID teacherId = accountUtils.getCurrentUserId();
        ExamInstance instance = examInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiên thi"));

        if (!instance.getTemplate().getCreatedBy().equals(teacherId)) {
            throw new BadRequestException("Truy cập bị từ chối đối với phiên thi này");
        }

        // Use MapStruct to update entity
        examInstanceMapper.updateEntity(instance, request);

        // Validate time range if both times are set
        if (instance.getEndAt().isBefore(instance.getStartAt())) {
            throw new BadRequestException("End time must be after start time");
        }

        ExamInstance savedInstance = examInstanceRepository.save(instance);

        // Update schedule khi có thay đổi về thời gian hoặc trạng thái
        examQuartzSchedulerService.updateExamSchedule(savedInstance);

        return examInstanceMapper.toResponse(savedInstance);
    }

    @Override
    public void deleteExamInstance(UUID instanceId) {
        UUID teacherId = accountUtils.getCurrentUserId();
        ExamInstance instance = examInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiên thi"));

        if (!instance.getTemplate().getCreatedBy().equals(teacherId)) {
            throw new BadRequestException("Truy cập bị từ chối đối với phiên thi này");
        }

        // Hủy tất cả scheduled tasks trước khi xóa
        examQuartzSchedulerService.cancelExamSchedules(instanceId.toString());

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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đề thi"));

        // Check if exam is accessible to students or completed (for viewing answers)
        if (!instance.getStatus().isAccessible() && instance.getStatus() != ExamInstanceStatus.COMPLETED) {
            throw new BadRequestException(
                String.format("Đề thi không khả dụng. Trạng thái hiện tại: %s (%s)",
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

        // If exam is completed, show answers; otherwise remove them
        Map<String, Object> content;
        if (instance.getStatus() == ExamInstanceStatus.COMPLETED) {
            content = instance.getTemplate().getContentJson(); // Include answers
        } else {
            content = examUtils.removeCorrectAnswers(instance.getTemplate().getContentJson()); // Remove answers
        }

        return examInstanceMapper.toContentResponse(instance, content);
    }

    @Override
    public SubmitExamResponse submitExam(String code, SubmitExamRequest request) {
        ExamInstance instance = examInstanceRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đề thi"));

        // Check if exam allows submissions
        if (!instance.getStatus().isSubmittable()) {
            throw new BadRequestException(
                String.format("Không được phép nộp bài thi. Trạng thái hiện tại: %s (%s)",
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

        

        // Grade the exam using new scoring configuration
        ExamGradingResult gradingResult = examGradingUtils.gradeExamWithScoringConfig(
            instance.getTemplate().getContentJson(),
            request.getAnswers(),
            instance.getTemplate().getScoringConfig(),
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
            log.error("Lỗi khi chuyển đổi câu trả lời sang JSON: {}", e.getMessage(), e);
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
            log.error("Lỗi khi tạo báo cáo Excel: {}", e.getMessage());
        }

        // Create response with additional info for completed exams
        SubmitExamResponse response = examSubmissionMapper.toSubmitResponse(savedSubmission);

        // If exam is completed, include answers and detailed results
        if (instance.getStatus() == ExamInstanceStatus.COMPLETED) {
            response.setExamCompleted(true);
            response.setExamContentWithAnswers(instance.getTemplate().getContentJson());

            // Get detailed results
            List<ExamResultDetail> details = examResultDetailRepository.findBySubmissionOrderByQuestionNumber(savedSubmission);
            response.setResultDetails(details.stream()
                    .map(examResultDetailMapper::toData)
                    .collect(Collectors.toList()));
        } else {
            response.setExamCompleted(false);
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamSubmissionResponse> getExamSubmissions(UUID instanceId) {
        UUID teacherId = accountUtils.getCurrentUserId();
        ExamInstance instance = examInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiên thi"));

        if (!instance.getTemplate().getCreatedBy().equals(teacherId)) {
            throw new BadRequestException("Truy cập bị từ chối đối với phiên thi này");
        }

        // Fetch submissions with result details for detailed analysis
        List<ExamSubmission> submissions = examSubmissionRepository.findByExamInstanceIdWithDetailsOrderBySubmittedAtDesc(instanceId);
        return submissions.stream()
                .map(submission -> {
                    ExamSubmissionResponse response = examSubmissionMapper.toResponse(submission);
                    // Sort result details by part and question number
                    if (response.getResultDetails() != null) {
                        response.getResultDetails().sort((a, b) -> {
                            // First sort by part order (PHẦN I, II, III)
                            int partComparison = Integer.compare(getPartOrder(a.getPartName()), getPartOrder(b.getPartName()));
                            if (partComparison != 0) {
                                return partComparison;
                            }
                            // Then sort by question number
                            if (a.getQuestionNumber() != null && b.getQuestionNumber() != null) {
                                int questionComparison = Integer.compare(a.getQuestionNumber(), b.getQuestionNumber());
                                if (questionComparison != 0) {
                                    return questionComparison;
                                }
                            } else if (a.getQuestionNumber() != null) {
                                return -1; // a has number, b doesn't - a comes first
                            } else if (b.getQuestionNumber() != null) {
                                return 1; // b has number, a doesn't - b comes first
                            }

                            // For Part II statements, sort by statement key (a, b, c, d)
                            if (a.getStatementKey() != null && b.getStatementKey() != null) {
                                return a.getStatementKey().compareTo(b.getStatementKey());
                            } else if (a.getStatementKey() != null) {
                                return 1; // statements come after main question
                            } else if (b.getStatementKey() != null) {
                                return -1; // statements come after main question
                            }

                            // Both null, sort by questionId as fallback
                            return a.getQuestionId().compareTo(b.getQuestionId());
                        });
                    }
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Resource generateExcelReport(UUID instanceId) {
        UUID teacherId = accountUtils.getCurrentUserId();
        ExamInstance instance = examInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiên thi"));

        if (!instance.getTemplate().getCreatedBy().equals(teacherId)) {
            throw new BadRequestException("Truy cập bị từ chối đối với phiên thi này");
        }

        return excelService.generateExcelReport(instanceId);
    }

    @Override
    public ExamInstanceResponse changeExamStatus(UUID instanceId, ChangeExamStatusRequest request) {
        UUID teacherId = accountUtils.getCurrentUserId();
        ExamInstance instance = examInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiên thi"));

        if (!instance.getTemplate().getCreatedBy().equals(teacherId)) {
            throw new BadRequestException("Truy cập bị từ chối đối với phiên thi này");
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

        // Quản lý scheduled tasks dựa trên status mới
        handleSchedulingForStatusChange(savedInstance, currentStatus, newStatus);

        return examInstanceMapper.toResponse(savedInstance);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getValidStatusTransitions(UUID instanceId) {
        UUID teacherId = accountUtils.getCurrentUserId();
        log.info("Getting valid status transitions for exam instance {} by teacher: {}", instanceId, teacherId);

        // Get current instance to check ownership and current status
        ExamInstanceResponse instance = getExamInstanceById(instanceId);
        ExamInstanceStatus currentStatus = instance.getStatus();

        // Build valid transitions based on current status
        Map<String, Object> response = new HashMap<>();
        response.put("currentStatus", currentStatus.getCode());
        response.put("currentStatusDescription", currentStatus.getDescription());

        List<Map<String, String>> validTransitions = new ArrayList<>();

        switch (currentStatus) {
            case DRAFT:
                validTransitions.add(createTransition("SCHEDULED", "Schedule exam for future start"));
                validTransitions.add(createTransition("ACTIVE", "Start exam immediately"));
                validTransitions.add(createTransition("CANCELLED", "Cancel exam permanently"));
                break;

            case SCHEDULED:
                validTransitions.add(createTransition("DRAFT", "Move back to draft for modifications"));
                validTransitions.add(createTransition("ACTIVE", "Start exam now (before scheduled time)"));
                validTransitions.add(createTransition("CANCELLED", "Cancel scheduled exam"));
                break;

            case ACTIVE:
                validTransitions.add(createTransition("PAUSED", "Pause exam temporarily"));
                validTransitions.add(createTransition("COMPLETED", "End exam early"));
                break;

            case PAUSED:
                validTransitions.add(createTransition("ACTIVE", "Resume exam"));
                validTransitions.add(createTransition("COMPLETED", "End exam while paused"));
                validTransitions.add(createTransition("CANCELLED", "Cancel exam permanently"));
                break;

            case COMPLETED:
            case CANCELLED:
                // Final states - no transitions allowed
                break;
        }

        response.put("validTransitions", validTransitions);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public StudentSubmissionResultResponse getStudentSubmissionResult(UUID submissionId) {
        log.info("Getting student submission result for submission: {}", submissionId);

        // Find submission with related data
        ExamSubmission submission = examSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài nộp"));

        ExamInstance instance = submission.getExamInstance();

        // Check if exam is completed - only allow viewing results when exam is completed
        if (instance.getStatus() != ExamInstanceStatus.COMPLETED) {
            throw new BadRequestException(
                String.format("Kết quả chưa khả dụng. Đề thi phải ở trạng thái hoàn thành. Trạng thái hiện tại: %s (%s)",
                    instance.getStatus().getCode(), instance.getStatus().getDescription())
            );
        }

        // Get detailed results
        List<ExamResultDetail> resultDetails = examResultDetailRepository.findBySubmissionOrderByQuestionNumber(submission);
        List<ExamResultDetailData> resultDetailData = resultDetails.stream()
                .map(examResultDetailMapper::toData)
                .collect(Collectors.toList());

        // Sort result details by part and question number (same logic as teacher view)
        resultDetailData.sort((a, b) -> {
            // First sort by part order (PHẦN I, II, III)
            int partComparison = Integer.compare(getPartOrder(a.getPartName()), getPartOrder(b.getPartName()));
            if (partComparison != 0) {
                return partComparison;
            }
            // Then sort by question number
            if (a.getQuestionNumber() != null && b.getQuestionNumber() != null) {
                int questionComparison = Integer.compare(a.getQuestionNumber(), b.getQuestionNumber());
                if (questionComparison != 0) {
                    return questionComparison;
                }
            } else if (a.getQuestionNumber() != null) {
                return -1; // a has number, b doesn't - a comes first
            } else if (b.getQuestionNumber() != null) {
                return 1; // b has number, a doesn't - b comes first
            }

            // For Part II statements, sort by statement key (a, b, c, d)
            if (a.getStatementKey() != null && b.getStatementKey() != null) {
                return a.getStatementKey().compareTo(b.getStatementKey());
            } else if (a.getStatementKey() != null) {
                return 1; // statements come after main question
            } else if (b.getStatementKey() != null) {
                return -1; // statements come after main question
            }

            // Both null, sort by questionId as fallback
            return a.getQuestionId().compareTo(b.getQuestionId());
        });

        // Calculate percentage
        Float percentage = (submission.getScore() / submission.getMaxScore()) * 100;

        // Use mapper to build response
        StudentSubmissionResultResponse response = studentSubmissionResultMapper.toStudentSubmissionResult(
                submission,
                percentage,
                instance.getTemplate().getContentJson(), // Exam content with answers
                resultDetailData
        );

        log.info("Successfully retrieved submission result for student: {} with score: {}/{}",
                submission.getStudentName(), submission.getScore(), submission.getMaxScore());

        return response;
    }

    private Map<String, String> createTransition(String status, String action) {
        ExamInstanceStatus statusEnum = ExamInstanceStatus.valueOf(status);
        Map<String, String> transition = new HashMap<>();
        transition.put("status", statusEnum.getCode());
        transition.put("description", statusEnum.getDescription());
        transition.put("action", action);
        return transition;
    }



    /**
     * Save detailed exam results
     */
    private void saveDetailedResults(ExamSubmission submission, List<ExamResultDetailData> details) {
        List<ExamResultDetail> resultDetails = examResultDetailMapper.toEntityList(details, submission);
        examResultDetailRepository.saveAll(resultDetails);
    }



    /**
     * Get part order for sorting (PHẦN I = 1, PHẦN II = 2, PHẦN III = 3)
     */
    private int getPartOrder(String partName) {
        if (partName == null) return 999; // Put null parts at the end

        if (partName.contains("I") && !partName.contains("II") && !partName.contains("III")) {
            return 1; // PHẦN I
        } else if (partName.contains("II")) {
            return 2; // PHẦN II
        } else if (partName.contains("III")) {
            return 3; // PHẦN III
        }
        return 999; // Unknown parts at the end
    }

    /**
     * Xử lý scheduling khi thay đổi status
     */
    private void handleSchedulingForStatusChange(ExamInstance instance, ExamInstanceStatus oldStatus, ExamInstanceStatus newStatus) {
        String instanceId = instance.getId().toString();

        switch (newStatus) {
            case SCHEDULED:
                examQuartzSchedulerService.scheduleExamStart(instance);
                break;

            case ACTIVE:
                examQuartzSchedulerService.cancelExamSchedules(instanceId);
                examQuartzSchedulerService.scheduleExamEnd(instance);
                break;

            case COMPLETED:
            case CANCELLED:
                // Cleanup và giải phóng bộ nhớ cho exam đã hoàn thành
                examQuartzSchedulerService.cleanupCompletedExam(instanceId);
                break;

            case PAUSED:
                examQuartzSchedulerService.cancelExamSchedules(instanceId);
                break;

            default:
                break;
        }
    }


}
