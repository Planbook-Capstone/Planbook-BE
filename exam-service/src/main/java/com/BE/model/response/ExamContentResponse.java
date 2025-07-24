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
public class ExamContentResponse {
    
    private UUID examInstanceId;
    private String examName;
    private String subject;
    private Integer grade;
    private Integer durationMinutes;
    private String school;
    private String examCode;
    private String atomicMasses;
    private Double totalScore;
    private Map<String, Object> contentJson; // Content without correct answers for students
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String code;
}
