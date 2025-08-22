package com.BE.mapper;

import com.BE.model.entity.ExamSubmission;
import com.BE.model.response.ExamResultDetailData;
import com.BE.model.response.StudentSubmissionResultResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {ExamResultDetailMapper.class})
public interface StudentSubmissionResultMapper {

    /**
     * Map ExamSubmission entity to StudentSubmissionResultResponse
     */
    @Mapping(target = "submissionId", source = "submission.id")
    @Mapping(target = "studentName", source = "submission.studentName")
    @Mapping(target = "score", source = "submission.score")
    @Mapping(target = "correctCount", source = "submission.correctCount")
    @Mapping(target = "totalQuestions", source = "submission.totalQuestions")
    @Mapping(target = "maxScore", source = "submission.maxScore")
    @Mapping(target = "percentage", source = "percentage")
    @Mapping(target = "submittedAt", source = "submission.submittedAt")
    
    // Exam instance information
    @Mapping(target = "examInstanceId", source = "submission.examInstance.id")
    @Mapping(target = "examInstanceCode", source = "submission.examInstance.code")
    @Mapping(target = "examTitle", source = "submission.examInstance.template.name")
    @Mapping(target = "examDescription", source = "submission.examInstance.description")
    @Mapping(target = "examStartAt", source = "submission.examInstance.startAt")
    @Mapping(target = "examEndAt", source = "submission.examInstance.endAt")
    
    // Exam content with answers and result details
    @Mapping(target = "examContentWithAnswers", source = "examContentWithAnswers")
    @Mapping(target = "resultDetails", source = "resultDetails")
    
    StudentSubmissionResultResponse toStudentSubmissionResult(
            ExamSubmission submission,
            Float percentage,
            Map<String, Object> examContentWithAnswers,
            List<ExamResultDetailData> resultDetails
    );
}
