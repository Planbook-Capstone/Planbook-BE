package com.BE.service.implementServices;

import com.BE.enums.ToolCodeEnum;
import com.BE.enums.ToolTypeEnum;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.exception.exceptions.WalletTokenException;
import com.BE.feign.IdentityServiceClient;
import com.BE.feign.ToolLogServiceClient;
import com.BE.mapper.StudentSubmissionMapper;
import com.BE.model.entity.AnswerSheetKey;
import com.BE.model.entity.GradingSession;
import com.BE.model.entity.StudentSubmission;
import com.BE.model.request.StudentSubmissionRequest;
import com.BE.model.request.ToolExecutionLogRequest;
import com.BE.model.request.WalletTokenRequest;
import com.BE.model.response.BookTypeResponse;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.StudentSubmissionResponse;
import com.BE.model.response.ToolExecutionLogResponse;
import com.BE.repository.GradingSessionRepository;
import com.BE.repository.StudentSubmissionRepository;
import com.BE.service.interfaceServices.StudentSubmissionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentSubmissionServiceImpl implements StudentSubmissionService {

    private final StudentSubmissionMapper studentSubmissionMapper;
    private final StudentSubmissionRepository studentSubmissionRepository;
    private final GradingSessionRepository gradingSessionRepository;
    private final ToolLogServiceClient toolLogServiceClient;
    private final IdentityServiceClient toolInternalServiceClient;
    private final ObjectMapper mapper;


    private void deductToken(ToolCodeEnum code, Integer tokenCostPerQuery, UUID idUser) {
        WalletTokenRequest request = new WalletTokenRequest();
        request.setAmount(tokenCostPerQuery);
        request.setUserId(idUser);
        String vi = ToolCodeEnum.toVietnamese(code);
        request.setDescription("Trừ token do sử dụng chức năng " + vi);
        try {
            toolInternalServiceClient.deduct(request);
        } catch (Exception e) {
            throw new WalletTokenException("Không đủ token trong ví để thực hiện hành động");
        }

    }

    @Override
    public StudentSubmissionResponse createAndGradeSubmission(StudentSubmissionRequest request) {


        StudentSubmission studentSubmission = studentSubmissionMapper.toEntity(request);

        GradingSession gradingSession = gradingSessionRepository.findById(request.getGradingSessionId()).orElseThrow(() -> new NotFoundException("Không tìm thấy mẫu OMR với ID: " + request.getGradingSessionId()));
        DataResponseDTO<BookTypeResponse> internalToolConfigResponse = toolInternalServiceClient.getBookTypeById(gradingSession.getBookTypeId());

        deductToken(internalToolConfigResponse.getData().getCode(), internalToolConfigResponse.getData().getTokenCostPerQuery(), gradingSession.getUserId());

        studentSubmission.setGradingSession(gradingSession);

        AnswerSheetKey answerSheetKey = gradingSession.getAnswerSheetKeys().stream()
                .filter(key -> key.getCode().equals(request.getExamCode()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Không tìm thấy answer sheet với examCode: " + request.getExamCode()));
        studentSubmission.setAnswerSheetKey(answerSheetKey);


        gradingSession.getStudentSubmissions().add(studentSubmission);
        answerSheetKey.getStudentSubmissions().add(studentSubmission);

        StudentSubmissionResponse response = studentSubmissionMapper.toResponse(studentSubmissionRepository.save(studentSubmission));

        Map<String, Object> input = new HashMap<>();

        input.put("imagePath", request.getImageBase64());
        input.put("answerSheetKey", answerSheetKey);

        Map<String, Object> answerMap = mapper.convertValue(
                request.getStudentAnswerJson(),
                new TypeReference<Map<String, Object>>() {}
        );

        ToolExecutionLogRequest toolExecutionLogRequest = ToolExecutionLogRequest.builder()
                .userId(gradingSession.getUserId())
                .toolId(internalToolConfigResponse.getData().getId())
                .toolType(ToolTypeEnum.INTERNAL)
                .code(ToolCodeEnum.EXAM_GRADING)
                .input(input)
                .output(answerMap)
                .bookId(null)
                .lessonIds(new ArrayList<>())
                .academicYearId(request.getAcademicYearId())
                .tokenUsed(internalToolConfigResponse.getData().getTokenCostPerQuery())
                .build();
       toolLogServiceClient.toolExecutionLog(toolExecutionLogRequest);

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

