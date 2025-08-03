package com.BE.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;
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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime submittedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private List<ExamResultDetailData> resultDetails; // Chi tiết từng câu hỏi
}
