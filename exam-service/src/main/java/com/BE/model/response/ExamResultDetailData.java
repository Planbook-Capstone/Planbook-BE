package com.BE.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultDetailData {
    private String questionId;
    private Integer questionNumber;
    private String partName;
    private String statementKey; // For Part II statements (a, b, c, d)
    private String question;
    private String studentAnswer;
    private String correctAnswer;
    private Boolean isCorrect;

    // Constructor without new fields for backward compatibility
    public ExamResultDetailData(String questionId, String studentAnswer, String correctAnswer, Boolean isCorrect) {
        this.questionId = questionId;
        this.questionNumber = null;
        this.partName = null;
        this.statementKey = null;
        this.question = null;
        this.studentAnswer = studentAnswer;
        this.correctAnswer = correctAnswer;
        this.isCorrect = isCorrect;
    }

    // Constructor with questionNumber but without other new fields
    public ExamResultDetailData(String questionId, Integer questionNumber, String studentAnswer, String correctAnswer, Boolean isCorrect) {
        this.questionId = questionId;
        this.questionNumber = questionNumber;
        this.partName = null;
        this.statementKey = null;
        this.question = null;
        this.studentAnswer = studentAnswer;
        this.correctAnswer = correctAnswer;
        this.isCorrect = isCorrect;
    }

    // Constructor for regular questions (without statementKey)
    public ExamResultDetailData(String questionId, Integer questionNumber, String partName, String question, String studentAnswer, String correctAnswer, Boolean isCorrect) {
        this.questionId = questionId;
        this.questionNumber = questionNumber;
        this.partName = partName;
        this.statementKey = null;
        this.question = question;
        this.studentAnswer = studentAnswer;
        this.correctAnswer = correctAnswer;
        this.isCorrect = isCorrect;
    }
}
