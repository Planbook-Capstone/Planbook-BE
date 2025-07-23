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
public class ExamTemplateResponse {

    private UUID id;
    private String name;
    private String subject;
    private Integer grade;
    private Integer durationMinutes;
    private UUID createdBy;
    private Map<String, Object> contentJson;
    private Map<String, Object> scoringConfig;
    private Double totalScore;
    private Integer version;
    private LocalDateTime createdAt;
}
