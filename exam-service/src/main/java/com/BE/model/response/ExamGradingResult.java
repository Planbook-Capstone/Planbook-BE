package com.BE.model.response;

import java.util.List;

/**
 * Exam grading result response model
 */
public class ExamGradingResult {
    private final Float score;
    private final Integer correctCount;
    private final Integer totalQuestions;
    private final Float maxScore;
    private final List<ExamResultDetailData> details;

    public ExamGradingResult(Float score, Integer correctCount, Integer totalQuestions, Float maxScore, List<ExamResultDetailData> details) {
        this.score = score;
        this.correctCount = correctCount;
        this.totalQuestions = totalQuestions;
        this.maxScore = maxScore;
        this.details = details;
    }

    public Float getScore() { 
        return score; 
    }
    
    public Integer getCorrectCount() { 
        return correctCount; 
    }
    
    public Integer getTotalQuestions() { 
        return totalQuestions; 
    }
    
    public Float getMaxScore() { 
        return maxScore; 
    }
    
    public List<ExamResultDetailData> getDetails() { 
        return details; 
    }
}
