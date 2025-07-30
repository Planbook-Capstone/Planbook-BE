package com.BE.mapper;

import com.BE.model.entity.ExamInstance;
import com.BE.model.entity.ExamSubmission;
import com.BE.model.request.SubmitExamRequest;
import com.BE.model.response.ExamSubmissionResponse;
import com.BE.model.response.SubmitExamResponse;
import com.BE.model.response.ExamGradingResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Map;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {ExamResultDetailMapper.class})
public interface ExamSubmissionMapper {

    /**
     * Map SubmitExamRequest to ExamSubmission entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "examInstance", source = "examInstance")
    @Mapping(target = "studentName", source = "request.studentName")
    @Mapping(target = "score", source = "gradingResult.score")
    @Mapping(target = "correctCount", source = "gradingResult.correctCount")
    @Mapping(target = "totalQuestions", source = "gradingResult.totalQuestions")
    @Mapping(target = "maxScore", source = "gradingResult.maxScore")
    @Mapping(target = "answersJson", source = "answersJson")
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "resultDetails", ignore = true)
    ExamSubmission toEntity(SubmitExamRequest request, ExamInstance examInstance,
                           ExamGradingResult gradingResult, Map<String, Object> answersJson);

    /**
     * Map ExamSubmission entity to ExamSubmissionResponse
     */
    @Mapping(target = "examInstanceId", source = "examInstance.id")
    @Mapping(target = "resultDetails", source = "resultDetails")
    ExamSubmissionResponse toResponse(ExamSubmission entity);

    /**
     * Map ExamSubmission entity to SubmitExamResponse
     */
    @Mapping(target = "submissionId", source = "id")
    @Mapping(target = "message", constant = "Exam submitted successfully")
    SubmitExamResponse toSubmitResponse(ExamSubmission entity);
}
