package com.BE.service.implementServices;

import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.StudentSubmissionMapper;
import com.BE.model.entity.AnswerSheetKey;
import com.BE.model.entity.GradingSession;
import com.BE.model.entity.StudentSubmission;
import com.BE.model.request.StudentSubmissionRequest;
import com.BE.model.response.StudentSubmissionResponse;
import com.BE.repository.GradingSessionRepository;
import com.BE.repository.StudentSubmissionRepository;
import com.BE.service.interfaceServices.StudentSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentSubmissionServiceImpl implements StudentSubmissionService {

    private final StudentSubmissionMapper studentSubmissionMapper;
    private final StudentSubmissionRepository studentSubmissionRepository;
    private final GradingSessionRepository gradingSessionRepository;


    @Override
    public StudentSubmissionResponse createAndGradeSubmission(StudentSubmissionRequest request) {
        StudentSubmission studentSubmission = studentSubmissionMapper.toEntity(request);
        GradingSession gradingSession = gradingSessionRepository.findById(request.getGradingSessionId()) .orElseThrow(() -> new NotFoundException("Không tìm thấy mẫu OMR với ID: " + request.getGradingSessionId()));

        studentSubmission.setGradingSession(gradingSession);

        AnswerSheetKey answerSheetKey = gradingSession.getAnswerSheetKeys().stream()
                .filter(key -> key.getCode().equals(request.getExamCode()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Không tìm thấy answer sheet với examCode: " + request.getExamCode()));
        studentSubmission.setAnswerSheetKey(answerSheetKey);


        gradingSession.getStudentSubmissions().add(studentSubmission);
        answerSheetKey.getStudentSubmissions().add(studentSubmission);

        StudentSubmissionResponse response = studentSubmissionMapper.toResponse(studentSubmissionRepository.save(studentSubmission));

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

