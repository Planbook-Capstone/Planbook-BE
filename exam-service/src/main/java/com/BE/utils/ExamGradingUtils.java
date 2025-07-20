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
            float maxScore = 100f; // Standard grading uses 100 as max

            return new ExamGradingResult(score, correctCount, totalQuestions, maxScore, details);

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
            return new ExamGradingResult(0f, 0, 0, 0f, new ArrayList<>());
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
                    if (templateQuestion.containsKey("subQuestions")) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> templateSubQuestions = (List<Map<String, Object>>) templateQuestion.get("subQuestions");
                        if (templateSubQuestions != null) {
                            for (Map<String, Object> templateSubQuestion : templateSubQuestions) {
                                String subQuestionId = (String) templateSubQuestion.get("id");
                                Object correctAnswerObj = templateSubQuestion.get("answer");
                                String correctAnswer = String.valueOf(correctAnswerObj);
                                String fullQuestionId = partName + "_Q" + templateQuestionId + "_" + subQuestionId;
                                details.add(new ExamResultDetailData(fullQuestionId, null, correctAnswer, false));
                            }
                            // Count as ONE question regardless of number of sub-questions
                            totalQuestions++;
                        }
                    } else {
                        String correctAnswer = getCorrectAnswer(templateQuestion);
                        String fullQuestionId = partName + "_Q" + templateQuestionId;
                        details.add(new ExamResultDetailData(fullQuestionId, null, correctAnswer != null ? correctAnswer : "N/A", false));
                        totalQuestions++;
                    }
                    continue;
                }

                // Handle different question types
                if (templateQuestion.containsKey("subQuestions")) {
                    // True/False questions with multiple sub-questions - count as ONE question
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> templateSubQuestions = (List<Map<String, Object>>) templateQuestion.get("subQuestions");
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> studentSubAnswers = studentQuestion != null ?
                        (List<Map<String, Object>>) studentQuestion.get("subAnswers") : new ArrayList<>();

                    if (templateSubQuestions != null) {
                        int statementCorrectCount = 0;
                        int statementTotalCount = 0;

                        for (Map<String, Object> templateSubQuestion : templateSubQuestions) {
                            String subQuestionId = (String) templateSubQuestion.get("id");
                            Object correctAnswerObj = templateSubQuestion.get("answer");
                            String correctAnswer = String.valueOf(correctAnswerObj);
                            String studentAnswer = null;

                            // Find corresponding student sub-answer
                            if (studentSubAnswers != null) {
                                for (Map<String, Object> studentSubAnswer : studentSubAnswers) {
                                    String studentSubId = (String) studentSubAnswer.get("id");
                                    if (subQuestionId != null && subQuestionId.equals(studentSubId)) {
                                        Object studentAnswerObj = studentSubAnswer.get("answer");
                                        studentAnswer = String.valueOf(studentAnswerObj);
                                        break;
                                    }
                                }
                            }

                            boolean isCorrect = correctAnswer.equals(studentAnswer);
                            if (isCorrect) {
                                statementCorrectCount++;
                            }

                            String fullQuestionId = partName + "_Q" + templateQuestionId + "_" + subQuestionId;
                            details.add(new ExamResultDetailData(fullQuestionId, studentAnswer, correctAnswer, isCorrect));
                            statementTotalCount++;
                        }

                        // Count the whole question as correct only if ALL sub-questions are correct
                        if (statementCorrectCount == statementTotalCount && statementTotalCount > 0) {
                            correctCount++;
                        }
                        totalQuestions++; // Count as ONE question regardless of number of sub-questions
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
                return new ExamGradingResult(0f, 0, 0, 0f, new ArrayList<>());
            }

            return new ExamGradingResult((float) correctCount / totalQuestions * 100, correctCount, totalQuestions, 100f, details);

        } catch (Exception e) {
            log.error("Error grading questions in part {}: {}", partName, e.getMessage(), e);
            throw new BadRequestException("Error grading questions in part: " + partName);
        }
    }

    /**
     * Calculate weighted score based on grading config
     */
    private float calculateWeightedScore(List<ExamResultDetailData> details,
                                       Map<String, Double> gradingConfig,
                                       Double totalScore) {
        if (details == null || details.isEmpty()) {
            return 0f;
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

        // Return raw earned score without any conversion
        return (float) earnedScore;
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
