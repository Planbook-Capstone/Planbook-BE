package com.BE.service.implementServices;

import com.BE.exception.ResourceNotFoundException;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.StudentSubmissionMapper;
import com.BE.model.entity.StudentSubmission;
import com.BE.model.request.StudentSubmissionRequest;
import com.BE.model.response.StudentSubmissionResponse;
import com.BE.repository.GradingSessionRepository;
import com.BE.repository.StudentSubmissionRepository;
import com.BE.service.interfaceServices.GradingService;
import com.BE.service.interfaceServices.StudentSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentSubmissionServiceImpl implements StudentSubmissionService {

    private final GradingService gradingService;
    private final StudentSubmissionMapper studentSubmissionMapper;
    private final StudentSubmissionRepository studentSubmissionRepository;
    private final GradingSessionRepository gradingSessionRepository;

    @Override
    public StudentSubmissionResponse createAndGradeSubmission(StudentSubmissionRequest request) {
        StudentSubmission gradedSubmission = gradingService.gradeSubmission(request);

        StudentSubmissionResponse response = studentSubmissionMapper.toResponse(gradedSubmission);
        response.setGradedAnswers(gradedSubmission.getStudentAnswerJson()); // Set the graded JSON in the response

        return response;
    }

    @Override
    public StudentSubmissionResponse getById(Long id) {
        StudentSubmission submission = studentSubmissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài làm của học sinh với ID: " + id));
        return studentSubmissionMapper.toResponse(submission);
    }

    @Override
    public java.util.List<StudentSubmissionResponse> getByGradingSessionId(Long gradingSessionId) {
        if (!gradingSessionRepository.existsById(gradingSessionId)) {
            throw new NotFoundException("Không tìm thấy phiên chấm điểm với ID: " + gradingSessionId);
        }
        return studentSubmissionRepository.findByGradingSessionId(gradingSessionId).stream()
                .map(studentSubmissionMapper::toResponse)
                .collect(Collectors.toList());
    }
}

