package com.BE.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitExamResponse {
    
    private UUID submissionId;
    private String studentName;
    private Float score;
    private Integer correctCount;
    private Integer totalQuestions;
    private LocalDateTime submittedAt;
    private String message;
}
