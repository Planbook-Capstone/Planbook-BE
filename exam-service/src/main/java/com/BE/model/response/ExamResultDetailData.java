package com.BE.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultDetailData {
    private String questionId;
    private String studentAnswer;
    private String correctAnswer;
    private Boolean isCorrect;
}
