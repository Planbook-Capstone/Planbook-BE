package com.BE.model.response;

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
public class SubmitExamResponse {
    
    private UUID submissionId;
    private String studentName;
    private Float score;
    private Integer correctCount;
    private Integer totalQuestions;
    private Float maxScore;
    private LocalDateTime submittedAt;
    private String message;

    // Thêm thông tin để hiển thị đáp án khi bài kiểm tra kết thúc
    private Boolean examCompleted;
    private Map<String, Object> examContentWithAnswers; // Chỉ có khi examCompleted = true
    private List<ExamResultDetailData> resultDetails;
}
