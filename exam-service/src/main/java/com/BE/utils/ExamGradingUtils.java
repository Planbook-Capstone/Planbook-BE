package com.BE.utils;

import com.BE.model.response.ExamResultDetailData;
import com.BE.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class ExamGradingUtils {

    /**
     * Grade exam with new flat answer format
     */
    public ExamGradingResult gradeExamWithFlatAnswers(Map<String, Object> templateContent,
                                                     List<Map<String, Object>> studentAnswers) {
        if (templateContent == null) {
            throw new BadRequestException("Template content is null");
        }

        if (studentAnswers == null || studentAnswers.isEmpty()) {
            throw new BadRequestException("Student answers are null or empty");
        }

        int totalQuestions = 0;
        int correctCount = 0;
        List<ExamResultDetailData> details = new ArrayList<>();

        try {
            // Create a map of questionId -> student answer for quick lookup
            Map<String, String> studentAnswerMap = new HashMap<>();
            for (Map<String, Object> studentAnswer : studentAnswers) {
                String questionId = (String) studentAnswer.get("questionId");
                String answer = (String) studentAnswer.get("answer");
                if (questionId != null && answer != null) {
                    studentAnswerMap.put(questionId, answer);
                }
            }

            // Process template content
            if (templateContent.containsKey("parts")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> templateParts = (List<Map<String, Object>>) templateContent.get("parts");

                if (templateParts == null || templateParts.isEmpty()) {
                    throw new BadRequestException("No parts found in template");
                }

                // Process each part
                for (Map<String, Object> templatePart : templateParts) {
                    String partName = (String) templatePart.get("part");
                    if (partName == null) {
                        partName = "UNKNOWN_PART";
                    }

                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> templateQuestions = (List<Map<String, Object>>) templatePart.get("questions");

                    if (templateQuestions != null && !templateQuestions.isEmpty()) {
                        for (Map<String, Object> templateQuestion : templateQuestions) {
                            String questionId = (String) templateQuestion.get("id");
                            if (questionId == null) {
                                log.warn("Template question has no id, skipping");
                                continue;
                            }

                            String correctAnswer = getCorrectAnswer(templateQuestion);
                            String studentAnswer = studentAnswerMap.get(questionId);

                            boolean isCorrect = correctAnswer != null && correctAnswer.equals(studentAnswer);
                            if (isCorrect) {
                                correctCount++;
                            }

                            String fullQuestionId = partName + "_Q" + questionId;
                            details.add(new ExamResultDetailData(fullQuestionId, studentAnswer,
                                correctAnswer != null ? correctAnswer : "N/A", isCorrect));
                            totalQuestions++;
                        }
                    }
                }
            } else {
                throw new BadRequestException("Template format is invalid - must contain 'parts'");
            }

            if (totalQuestions == 0) {
                throw new BadRequestException("No questions were found to grade. Please check your answer format.");
            }

            float score = (float) correctCount / totalQuestions * 100;
            float maxScore = 100f;

            return new ExamGradingResult(score, correctCount, totalQuestions, maxScore, details);

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error grading exam: {}", e.getMessage(), e);
            throw new BadRequestException("Error processing exam answers. Please check your answer format and try again.");
        }
    }

    /**
     * Grade exam with custom configuration using flat answer format
     */
    public ExamGradingResult gradeExamWithFlatAnswersAndCustomConfig(Map<String, Object> templateContent,
                                                                   List<Map<String, Object>> studentAnswers,
                                                                   Map<String, Double> customGradingConfig,
                                                                   Double customTotalScore) {
        if (templateContent == null) {
            throw new BadRequestException("Template content is null");
        }

        if (studentAnswers == null || studentAnswers.isEmpty()) {
            throw new BadRequestException("Student answers are null or empty");
        }

        int totalQuestions = 0;
        int correctCount = 0;
        List<ExamResultDetailData> details = new ArrayList<>();

        try {
            // Create a map of questionId -> student answer for quick lookup
            Map<String, String> studentAnswerMap = new HashMap<>();
            for (Map<String, Object> studentAnswer : studentAnswers) {
                String questionId = (String) studentAnswer.get("questionId");
                String answer = (String) studentAnswer.get("answer");
                if (questionId != null && answer != null) {
                    studentAnswerMap.put(questionId, answer);
                }
            }

            // Process template content
            if (templateContent.containsKey("parts")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> templateParts = (List<Map<String, Object>>) templateContent.get("parts");

                if (templateParts == null || templateParts.isEmpty()) {
                    throw new BadRequestException("No parts found in template");
                }

                // Process each part
                for (Map<String, Object> templatePart : templateParts) {
                    String partName = (String) templatePart.get("part");
                    if (partName == null) {
                        partName = "UNKNOWN_PART";
                    }

                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> templateQuestions = (List<Map<String, Object>>) templatePart.get("questions");

                    if (templateQuestions != null && !templateQuestions.isEmpty()) {
                        for (Map<String, Object> templateQuestion : templateQuestions) {
                            String questionId = (String) templateQuestion.get("id");
                            if (questionId == null) {
                                log.warn("Template question has no id, skipping");
                                continue;
                            }

                            String correctAnswer = getCorrectAnswer(templateQuestion);
                            String studentAnswer = studentAnswerMap.get(questionId);

                            boolean isCorrect = correctAnswer != null && correctAnswer.equals(studentAnswer);
                            if (isCorrect) {
                                correctCount++;
                            }

                            String fullQuestionId = partName + "_Q" + questionId;
                            details.add(new ExamResultDetailData(fullQuestionId, studentAnswer,
                                correctAnswer != null ? correctAnswer : "N/A", isCorrect));
                            totalQuestions++;
                        }
                    }
                }
            } else {
                throw new BadRequestException("Template format is invalid - must contain 'parts'");
            }

            if (totalQuestions == 0) {
                throw new BadRequestException("No questions were found to grade. Please check your answer format.");
            }

            // Calculate score using custom grading config if provided
            float score;
            float maxScore;
            if (customGradingConfig != null && !customGradingConfig.isEmpty()) {
                WeightedScoreResult result = calculateWeightedScoreWithMax(details, customGradingConfig, customTotalScore);
                score = result.score;
                maxScore = result.maxScore;
            } else {
                // Return raw correct count without any conversion
                score = (float) correctCount;
                maxScore = (float) totalQuestions;
            }

            return new ExamGradingResult(score, correctCount, totalQuestions, maxScore, details);

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error grading exam: {}", e.getMessage(), e);
            throw new BadRequestException("Error processing exam answers. Please check your answer format and try again.");
        }
    }









    /**
     * Calculate weighted score with max score information
     */
    private WeightedScoreResult calculateWeightedScoreWithMax(List<ExamResultDetailData> details,
                                                            Map<String, Double> gradingConfig,
                                                            Double totalScore) {
        if (details == null || details.isEmpty()) {
            return new WeightedScoreResult(0f, 0f);
        }

        double earnedScore = 0.0;
        double maxPossibleScore = 0.0;

        // Group details by part and main question
        Map<String, Map<String, List<ExamResultDetailData>>> detailsByPartAndQuestion = new HashMap<>();
        for (ExamResultDetailData detail : details) {
            String questionId = detail.getQuestionId();
            String partName = extractPartName(questionId);
            String mainQuestionId = extractMainQuestionId(questionId);

            detailsByPartAndQuestion
                .computeIfAbsent(partName, k -> new HashMap<>())
                .computeIfAbsent(mainQuestionId, k -> new ArrayList<>())
                .add(detail);
        }

        // Calculate score for each part
        for (Map.Entry<String, Map<String, List<ExamResultDetailData>>> partEntry : detailsByPartAndQuestion.entrySet()) {
            String partName = partEntry.getKey();
            Map<String, List<ExamResultDetailData>> questionsInPart = partEntry.getValue();

            Double partWeight = gradingConfig.get(partName);
            if (partWeight == null) {
                log.warn("No grading config found for part: {}, using default weight 1.0", partName);
                partWeight = 1.0;
            }

            int correctQuestionsInPart = 0;
            int totalQuestionsInPart = questionsInPart.size();

            // For each main question in this part
            for (Map.Entry<String, List<ExamResultDetailData>> questionEntry : questionsInPart.entrySet()) {
                List<ExamResultDetailData> questionDetails = questionEntry.getValue();

                // Check if this main question is correct
                // For sub-questions (true/false), ALL must be correct
                // For single questions, just check the one answer
                boolean isMainQuestionCorrect = questionDetails.stream()
                    .allMatch(detail -> detail.getIsCorrect());

                if (isMainQuestionCorrect) {
                    correctQuestionsInPart++;
                }
            }

            // Calculate part score
            double partScore = correctQuestionsInPart * partWeight;
            earnedScore += partScore;

            // Calculate max possible score for this part
            double maxPartScore = totalQuestionsInPart * partWeight;
            maxPossibleScore += maxPartScore;

            log.info("Part {}: {}/{} questions correct, weight={}, score={}/{}",
                partName, correctQuestionsInPart, totalQuestionsInPart, partWeight, partScore, maxPartScore);
        }

        return new WeightedScoreResult((float) earnedScore, (float) maxPossibleScore);
    }

    /**
     * Result class for weighted score calculation
     */
    private static class WeightedScoreResult {
        final float score;
        final float maxScore;

        WeightedScoreResult(float score, float maxScore) {
            this.score = score;
            this.maxScore = maxScore;
        }
    }

    /**
     * Extract part name from question ID
     */
    private String extractPartName(String questionId) {
        if (questionId == null) {
            return "UNKNOWN";
        }

        // Question ID format: "PHẦN I_Q1" or "PHẦN II_Q7_7a"
        int underscoreIndex = questionId.indexOf("_");
        if (underscoreIndex > 0) {
            return questionId.substring(0, underscoreIndex);
        }

        return "UNKNOWN";
    }

    /**
     * Extract main question ID from question ID
     */
    private String extractMainQuestionId(String questionId) {
        if (questionId == null) {
            return "UNKNOWN";
        }

        // Question ID format: "PHẦN I_Q1" or "PHẦN II_Q7_7a"
        // We want to extract "Q1" or "Q7" (the main question part)
        int underscoreIndex = questionId.indexOf("_");
        if (underscoreIndex > 0) {
            String remaining = questionId.substring(underscoreIndex + 1);
            // Check if there's another underscore (for sub-questions)
            int secondUnderscoreIndex = remaining.indexOf("_");
            if (secondUnderscoreIndex > 0) {
                return remaining.substring(0, secondUnderscoreIndex); // "Q7"
            } else {
                return remaining; // "Q1"
            }
        }

        return "UNKNOWN";
    }

    /**
     * Get correct answer from question
     */
    private String getCorrectAnswer(Map<String, Object> question) {
        if (question.containsKey("correctAnswer")) {
            return String.valueOf(question.get("correctAnswer"));
        }
        if (question.containsKey("answer")) {
            return String.valueOf(question.get("answer"));
        }
        return null;
    }

    /**
     * Exam grading result class
     */
    public static class ExamGradingResult {
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

        public Float getScore() { return score; }
        public Integer getCorrectCount() { return correctCount; }
        public Integer getTotalQuestions() { return totalQuestions; }
        public Float getMaxScore() { return maxScore; }
        public List<ExamResultDetailData> getDetails() { return details; }
    }
}
