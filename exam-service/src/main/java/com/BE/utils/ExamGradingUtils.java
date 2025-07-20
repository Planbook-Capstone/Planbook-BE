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
     * Grade exam with custom configuration
     */
    public ExamGradingResult gradeExamWithCustomConfig(Map<String, Object> templateContent,
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
            // Handle new format (parts with questions)
            if (templateContent.containsKey("parts")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> templateParts = (List<Map<String, Object>>) templateContent.get("parts");

                if (templateParts == null || templateParts.isEmpty()) {
                    throw new BadRequestException("No parts found in template");
                }

                // Match template parts with student answers by part name
                for (Map<String, Object> templatePart : templateParts) {
                    String templatePartName = (String) templatePart.get("part");

                    if (templatePartName == null) {
                        log.warn("Template part has no name, skipping");
                        continue;
                    }

                    // Find corresponding student part
                    Map<String, Object> studentPart = null;
                    for (Map<String, Object> saPart : studentAnswers) {
                        String studentPartName = (String) saPart.get("part");
                        if (templatePartName.equals(studentPartName)) {
                            studentPart = saPart;
                            break;
                        }
                    }

                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> templateQuestions = (List<Map<String, Object>>) templatePart.get("questions");
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> studentQuestions = studentPart != null ?
                        (List<Map<String, Object>>) studentPart.get("questions") : new ArrayList<>();

                    if (templateQuestions != null && !templateQuestions.isEmpty()) {
                        ExamGradingResult partResult = gradeQuestionsInPart(templateQuestions, studentQuestions, templatePartName);
                        totalQuestions += partResult.getTotalQuestions();
                        correctCount += partResult.getCorrectCount();
                        details.addAll(partResult.getDetails());
                    }
                }
            } else {
                throw new BadRequestException("Template format is invalid - must contain 'parts'");
            }

            if (totalQuestions == 0) {
                throw new BadRequestException("No questions were found to grade. Please check your answer format.");
            }

            float score = (float) correctCount / totalQuestions * 100;

            return new ExamGradingResult(score, correctCount, totalQuestions, details);

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error grading exam: {}", e.getMessage(), e);
            throw new BadRequestException("Error processing exam answers. Please check your answer format and try again.");
        }
    }

    /**
     * Grade exam (standard grading)
     */
    public ExamGradingResult gradeExam(Map<String, Object> templateContent, List<Map<String, Object>> studentAnswers) {
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
            // Handle both old format (questions) and new format (parts)
            if (templateContent.containsKey("questions")) {
                // Old format - direct questions
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> templateQuestions = (List<Map<String, Object>>) templateContent.get("questions");

                if (templateQuestions == null || templateQuestions.isEmpty()) {
                    throw new BadRequestException("No questions found in template");
                }

                // For old format, student answers should be in first element
                if (studentAnswers.size() > 0 && studentAnswers.get(0).containsKey("questions")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> studentQuestions = (List<Map<String, Object>>) studentAnswers.get(0).get("questions");

                    ExamGradingResult result = gradeQuestionsInPart(templateQuestions, studentQuestions, "Main");
                    totalQuestions = result.getTotalQuestions();
                    correctCount = result.getCorrectCount();
                    details = result.getDetails();
                } else {
                    throw new BadRequestException("Student answers format doesn't match template format");
                }

            } else if (templateContent.containsKey("parts")) {
                // New format - parts with questions
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> templateParts = (List<Map<String, Object>>) templateContent.get("parts");

                if (templateParts == null || templateParts.isEmpty()) {
                    throw new BadRequestException("No parts found in template");
                }

                // Match template parts with student answers by part name
                for (Map<String, Object> templatePart : templateParts) {
                    String templatePartName = (String) templatePart.get("part");

                    if (templatePartName == null) {
                        log.warn("Template part has no name, skipping");
                        continue;
                    }

                    // Find corresponding student part
                    Map<String, Object> studentPart = null;
                    for (Map<String, Object> saPart : studentAnswers) {
                        String studentPartName = (String) saPart.get("part");
                        if (templatePartName.equals(studentPartName)) {
                            studentPart = saPart;
                            break;
                        }
                    }

                    if (studentPart == null) {
                        log.info("Student didn't answer part: {}, skipping", templatePartName);
                        continue;
                    }

                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> templateQuestions = (List<Map<String, Object>>) templatePart.get("questions");
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> studentQuestions = (List<Map<String, Object>>) studentPart.get("questions");

                    if (templateQuestions != null && !templateQuestions.isEmpty()) {
                        ExamGradingResult partResult = gradeQuestionsInPart(templateQuestions, studentQuestions, templatePartName);
                        totalQuestions += partResult.getTotalQuestions();
                        correctCount += partResult.getCorrectCount();
                        details.addAll(partResult.getDetails());
                    }
                }
            } else {
                throw new BadRequestException("Template format is invalid - must contain 'questions' or 'parts'");
            }

            if (totalQuestions == 0) {
                throw new BadRequestException("No questions were found to grade. Please check your answer format.");
            }

            float score = (float) correctCount / totalQuestions * 100;

            return new ExamGradingResult(score, correctCount, totalQuestions, details);

        } catch (BadRequestException e) {
            // Re-throw BadRequestException as-is
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error grading exam: {}", e.getMessage(), e);
            throw new BadRequestException("Error processing exam answers. Please check your answer format and try again.");
        }
    }

    /**
     * Grade questions in a specific part
     */
    public ExamGradingResult gradeQuestionsInPart(List<Map<String, Object>> templateQuestions,
                                                  List<Map<String, Object>> studentQuestions,
                                                  String partName) {
        if (templateQuestions == null || templateQuestions.isEmpty()) {
            return new ExamGradingResult(0f, 0, 0, new ArrayList<>());
        }

        if (studentQuestions == null) {
            studentQuestions = new ArrayList<>();
        }

        int totalQuestions = 0;
        int correctCount = 0;
        List<ExamResultDetailData> details = new ArrayList<>();

        try {
            // Match questions by ID
            for (Map<String, Object> templateQuestion : templateQuestions) {
                Object templateQuestionIdObj = templateQuestion.get("id");
                if (templateQuestionIdObj == null) {
                    log.warn("Template question has no id, skipping");
                    continue;
                }

                String templateQuestionId = String.valueOf(templateQuestionIdObj);

                // Find corresponding student question by ID
                Map<String, Object> studentQuestion = null;
                for (Map<String, Object> sq : studentQuestions) {
                    Object studentQuestionIdObj = sq.get("id");
                    if (studentQuestionIdObj != null && templateQuestionId.equals(String.valueOf(studentQuestionIdObj))) {
                        studentQuestion = sq;
                        break;
                    }
                }

                if (studentQuestion == null) {
                    log.info("Student didn't answer question ID: {} in part: {}", templateQuestionId, partName);
                    // Still count as a question but mark as incorrect
                    if (templateQuestion.containsKey("statements")) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> templateStatements = (Map<String, Object>) templateQuestion.get("statements");
                        if (templateStatements != null) {
                            for (String statementKey : templateStatements.keySet()) {
                                String fullQuestionId = partName + "_Q" + templateQuestionId + "_" + statementKey;
                                details.add(new ExamResultDetailData(fullQuestionId, null, "N/A", false));
                            }
                            // Count as ONE question regardless of number of statements
                            totalQuestions++;
                        }
                    } else {
                        String fullQuestionId = partName + "_Q" + templateQuestionId;
                        details.add(new ExamResultDetailData(fullQuestionId, null, "N/A", false));
                        totalQuestions++;
                    }
                    continue;
                }

                // Handle different question types
                if (templateQuestion.containsKey("statements")) {
                    // True/False questions with multiple statements - count as ONE question
                    @SuppressWarnings("unchecked")
                    Map<String, Object> templateStatements = (Map<String, Object>) templateQuestion.get("statements");
                    @SuppressWarnings("unchecked")
                    Map<String, Object> studentStatements = (Map<String, Object>) studentQuestion.get("statements");

                    if (templateStatements != null) {
                        int statementCorrectCount = 0;
                        int statementTotalCount = 0;

                        for (String statementKey : templateStatements.keySet()) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> templateStatement = (Map<String, Object>) templateStatements.get(statementKey);
                            String correctAnswer = String.valueOf(templateStatement.get("answer"));
                            String studentAnswer = null;

                            if (studentStatements != null) {
                                studentAnswer = (String) studentStatements.get(statementKey);
                            }

                            boolean isCorrect = correctAnswer.equals(studentAnswer);
                            if (isCorrect) {
                                statementCorrectCount++;
                            }

                            String fullQuestionId = partName + "_Q" + templateQuestionId + "_" + statementKey;
                            details.add(new ExamResultDetailData(fullQuestionId, studentAnswer, correctAnswer, isCorrect));
                            statementTotalCount++;
                        }

                        // Count the whole question as correct only if ALL statements are correct
                        if (statementCorrectCount == statementTotalCount && statementTotalCount > 0) {
                            correctCount++;
                        }
                        totalQuestions++; // Count as ONE question regardless of number of statements
                    }
                } else {
                    // Regular questions (multiple choice, short answer)
                    String correctAnswer = getCorrectAnswer(templateQuestion);
                    String studentAnswer = (String) studentQuestion.get("answer");

                    boolean isCorrect = correctAnswer != null && correctAnswer.equals(studentAnswer);
                    if (isCorrect) {
                        correctCount++;
                    }

                    String fullQuestionId = partName + "_Q" + templateQuestionId;
                    details.add(new ExamResultDetailData(fullQuestionId, studentAnswer, correctAnswer, isCorrect));
                    totalQuestions++;
                }
            }

            if (totalQuestions == 0) {
                return new ExamGradingResult(0f, 0, 0, new ArrayList<>());
            }

            return new ExamGradingResult((float) correctCount / totalQuestions * 100, correctCount, totalQuestions, details);

        } catch (Exception e) {
            log.error("Error grading questions in part {}: {}", partName, e.getMessage(), e);
            throw new BadRequestException("Error grading questions in part: " + partName);
        }
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
        private final List<ExamResultDetailData> details;

        public ExamGradingResult(Float score, Integer correctCount, Integer totalQuestions, List<ExamResultDetailData> details) {
            this.score = score;
            this.correctCount = correctCount;
            this.totalQuestions = totalQuestions;
            this.details = details;
        }

        public Float getScore() { return score; }
        public Integer getCorrectCount() { return correctCount; }
        public Integer getTotalQuestions() { return totalQuestions; }
        public List<ExamResultDetailData> getDetails() { return details; }
    }
}
