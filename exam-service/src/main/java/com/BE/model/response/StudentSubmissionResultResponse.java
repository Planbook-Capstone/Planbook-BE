package com.BE.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSubmissionResultResponse {
    
    // Submission basic information
    private UUID submissionId;
    private String studentName;
    private Float score;
    private Integer correctCount;
    private Integer totalQuestions;
    private Float maxScore;
    private Float percentage;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime submittedAt;
    
    // Exam instance information
    private UUID examInstanceId;
    private String examInstanceCode;
    private String examTitle;
    private String examDescription;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime examStartAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime examEndAt;
    
    // Exam content with correct answers revealed
    private Map<String, Object> examContentWithAnswers;
    
    // Detailed results for each question
    private List<ExamResultDetailData> resultDetails;
}
