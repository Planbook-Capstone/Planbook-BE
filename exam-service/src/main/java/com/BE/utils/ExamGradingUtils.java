package com.BE.utils;

import com.BE.model.response.ExamResultDetailData;
import com.BE.model.response.ExamGradingResult;
import com.BE.model.dto.ScoringConfig;
import com.BE.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExamGradingUtils {

    private final ScoringConfigUtils scoringConfigUtils;

    /**
     * Grade exam with new scoring configuration using flat answer format
     */
    public ExamGradingResult gradeExamWithScoringConfig(Map<String, Object> templateContent,
                                                       List<Map<String, Object>> studentAnswers,
                                                       Map<String, Object> scoringConfigMap,
                                                       Double totalScore) {
        if (templateContent == null) {
            throw new BadRequestException("Template content is null");
        }

        if (studentAnswers == null || studentAnswers.isEmpty()) {
            throw new BadRequestException("Student answers are null or empty");
        }

        // Convert scoring config map to object
        ScoringConfig scoringConfig = scoringConfigUtils.mapToScoringConfig(scoringConfigMap);

        int totalQuestions = 0;
        int correctCount = 0;
        List<ExamResultDetailData> details = new ArrayList<>();

        // Track scores by part for new scoring system
        double part1Score = 0.0;
        double part2Score = 0.0;
        double part3Score = 0.0;

        try {
            // Create a map of questionId -> student answer for quick lookup (only for non-statement questions)
            Map<String, String> studentAnswerMap = new HashMap<>();
            for (Map<String, Object> studentAnswer : studentAnswers) {
                String questionId = (String) studentAnswer.get("questionId");
                Object answerObj = studentAnswer.get("answer");
                // Only add to map if answer is a String (not a Map for statement questions)
                if (questionId != null && answerObj instanceof String) {
                    studentAnswerMap.put(questionId, (String) answerObj);
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

                            // Handle different question types
                            if (templateQuestion.containsKey("statements")) {
                                // True/False questions with statements (Part II)
                                Map<String, Integer> statementResult = gradeStatementQuestionWithCounts(templateQuestion, studentAnswers, partName, details);
                                boolean isCorrect = statementResult.get("isCorrect") == 1;
                                int correctStatements = statementResult.get("correctStatements");
                                int totalStatements = statementResult.get("totalStatements");

                                if (isCorrect) {
                                    correctCount++;
                                }

                                // Calculate Part II score using new scoring system
                                double questionScore = scoringConfigUtils.calculatePart2Score(correctStatements, totalStatements, scoringConfig);
                                part2Score += questionScore;
                                totalQuestions++;
                            } else {
                                // Regular questions (multiple choice, short answer)
                                String correctAnswer = getCorrectAnswer(templateQuestion);
                                String studentAnswer = studentAnswerMap.get(questionId);

                                boolean isCorrect = correctAnswer != null && correctAnswer.equals(studentAnswer);
                                if (isCorrect) {
                                    correctCount++;
                                }

                                String fullQuestionId = partName + "_Q" + questionId;
                                details.add(new ExamResultDetailData(fullQuestionId, studentAnswer,
                                    correctAnswer != null ? correctAnswer : "N/A", isCorrect));

                                // Determine part and calculate score
                                if (partName.contains("I") && !partName.contains("II") && !partName.contains("III")) {
                                    // Part I
                                    if (isCorrect) {
                                        part1Score += scoringConfig.getPart1Score() != null ? scoringConfig.getPart1Score() : 0.25;
                                    }
                                } else if (partName.contains("III")) {
                                    // Part III
                                    if (isCorrect) {
                                        part3Score += scoringConfig.getPart3Score() != null ? scoringConfig.getPart3Score() : 0.25;
                                    }
                                }
                                totalQuestions++;
                            }
                        }
                    }
                }
            } else {
                throw new BadRequestException("Template format is invalid - must contain 'parts'");
            }

            if (totalQuestions == 0) {
                throw new BadRequestException("No questions were found to grade. Please check your answer format.");
            }

            // Calculate final score
            double finalScore = part1Score + part2Score + part3Score;
            float score = scoringConfigUtils.roundToTwoDecimalsFloat(finalScore);
            float maxScore = totalScore != null ? totalScore.floatValue() : 10.0f;

            log.info("Grading completed - Part I: {}, Part II: {}, Part III: {}, Total: {}",
                     part1Score, part2Score, part3Score, finalScore);

            return new ExamGradingResult(score, correctCount, totalQuestions, maxScore, details);

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error grading exam: {}", e.getMessage(), e);
            throw new BadRequestException("Error processing exam answers. Please check your answer format and try again.");
        }
    }

    /**
     * Grade statement-based questions (True/False with multiple statements)
     * Returns map with correctStatements, totalStatements, and isCorrect (1 if all correct, 0 otherwise)
     */
    private Map<String, Integer> gradeStatementQuestionWithCounts(Map<String, Object> templateQuestion,
                                                                List<Map<String, Object>> studentAnswers,
                                                                String partName,
                                                                List<ExamResultDetailData> details) {
        String questionId = (String) templateQuestion.get("id");

        // Find student answer for this question
        Map<String, Object> studentAnswerObj = null;
        for (Map<String, Object> studentAnswer : studentAnswers) {
            if (questionId.equals(studentAnswer.get("questionId"))) {
                studentAnswerObj = studentAnswer;
                break;
            }
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> templateStatements = (Map<String, Object>) templateQuestion.get("statements");

        if (templateStatements == null || templateStatements.isEmpty()) {
            log.warn("Template question {} has no statements", questionId);
            Map<String, Integer> emptyResult = new HashMap<>();
            emptyResult.put("correctStatements", 0);
            emptyResult.put("totalStatements", 0);
            emptyResult.put("isCorrect", 0);
            return emptyResult;
        }

        // Get student answers for statements
        @SuppressWarnings("unchecked")
        Map<String, Object> studentStatementAnswers = studentAnswerObj != null ?
            (Map<String, Object>) studentAnswerObj.get("answer") : new HashMap<>();

        if (studentStatementAnswers == null) {
            studentStatementAnswers = new HashMap<>();
        }

        int correctStatements = 0;
        int totalStatements = 0;

        // Check each statement
        for (Map.Entry<String, Object> statementEntry : templateStatements.entrySet()) {
            String statementKey = statementEntry.getKey(); // a, b, c, d
            @SuppressWarnings("unchecked")
            Map<String, Object> statementData = (Map<String, Object>) statementEntry.getValue();

            Boolean correctAnswer = (Boolean) statementData.get("answer");
            Object studentAnswerObj2 = studentStatementAnswers.get(statementKey);
            Boolean studentAnswer = studentAnswerObj2 instanceof Boolean ? (Boolean) studentAnswerObj2 : null;

            boolean isStatementCorrect = correctAnswer != null && correctAnswer.equals(studentAnswer);
            if (isStatementCorrect) {
                correctStatements++;
            }

            // Add detail for each statement
            String fullQuestionId = partName + "_Q" + questionId + "_" + statementKey;
            String studentAnswerStr = studentAnswer != null ? studentAnswer.toString() : "null";
            String correctAnswerStr = correctAnswer != null ? correctAnswer.toString() : "null";

            details.add(new ExamResultDetailData(fullQuestionId, studentAnswerStr, correctAnswerStr, isStatementCorrect));
            totalStatements++;
        }

        // Question is correct only if ALL statements are correct
        boolean isAllCorrect = correctStatements == totalStatements && totalStatements > 0;

        Map<String, Integer> result = new HashMap<>();
        result.put("correctStatements", correctStatements);
        result.put("totalStatements", totalStatements);
        result.put("isCorrect", isAllCorrect ? 1 : 0);

        return result;
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

}
