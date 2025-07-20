package com.BE.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamSubmissionResponse {
    
    private UUID id;
    private UUID examInstanceId;
    private String studentName;
    private Float score;
    private Integer correctCount;
    private Integer totalQuestions;
    private Float maxScore;
    private Map<String, Object> answersJson;
    private LocalDateTime submittedAt;
}
