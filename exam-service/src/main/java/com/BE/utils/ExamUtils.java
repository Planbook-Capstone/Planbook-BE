package com.BE.utils;

import com.BE.model.response.ExamResultDetailData;
import com.BE.exception.BadRequestException;
import com.BE.repository.ExamInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExamUtils {

    private final ExamInstanceRepository examInstanceRepository;

    /**
     * Generate unique exam code
     */
    public String generateUniqueCode() {
        String code;
        do {
            code = generateRandomCode();
        } while (examInstanceRepository.existsByCode(code));
        return code;
    }

    /**
     * Generate random 6-character code
     */
    private String generateRandomCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return code.toString();
    }

    /**
     * Remove correct answers from exam content for students
     */
    public Map<String, Object> removeCorrectAnswers(Map<String, Object> contentJson) {
        try {
            // Create a deep copy of the content
            Map<String, Object> studentContent = new HashMap<>(contentJson);

            // Handle old format (direct questions)
            if (studentContent.containsKey("questions")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> questions = (List<Map<String, Object>>) studentContent.get("questions");
                List<Map<String, Object>> studentQuestions = removeAnswersFromQuestions(questions);
                studentContent.put("questions", studentQuestions);
            }

            // Handle new format (parts with questions)
            if (studentContent.containsKey("parts")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> parts = (List<Map<String, Object>>) studentContent.get("parts");

                List<Map<String, Object>> studentParts = new ArrayList<>();
                for (Map<String, Object> part : parts) {
                    Map<String, Object> studentPart = new HashMap<>(part);

                    if (studentPart.containsKey("questions")) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> questions = (List<Map<String, Object>>) studentPart.get("questions");
                        List<Map<String, Object>> studentQuestions = removeAnswersFromQuestions(questions);
                        studentPart.put("questions", studentQuestions);
                    }

                    studentParts.add(studentPart);
                }
                studentContent.put("parts", studentParts);
            }

            return studentContent;
        } catch (Exception e) {
            log.error("Error removing correct answers: {}", e.getMessage());
            return contentJson;
        }
    }

    /**
     * Remove answers from list of questions
     */
    private List<Map<String, Object>> removeAnswersFromQuestions(List<Map<String, Object>> questions) {
        List<Map<String, Object>> studentQuestions = new ArrayList<>();

        for (Map<String, Object> question : questions) {
            Map<String, Object> studentQuestion = new HashMap<>(question);

            // Remove correct answer fields
            studentQuestion.remove("correctAnswer");
            studentQuestion.remove("correctAnswers");
            studentQuestion.remove("answer");

            // For true/false questions, remove answers from statements
            if (studentQuestion.containsKey("statements")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> statements = (Map<String, Object>) studentQuestion.get("statements");
                Map<String, Object> studentStatements = new HashMap<>();

                for (Map.Entry<String, Object> entry : statements.entrySet()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> statement = (Map<String, Object>) entry.getValue();
                    Map<String, Object> studentStatement = new HashMap<>(statement);
                    studentStatement.remove("answer");
                    studentStatements.put(entry.getKey(), studentStatement);
                }

                studentQuestion.put("statements", studentStatements);
            }

            studentQuestions.add(studentQuestion);
        }

        return studentQuestions;
    }

    /**
     * Validate exam content structure
     */
    public void validateExamContent(Map<String, Object> contentJson) {
        if (contentJson == null) {
            throw new BadRequestException("Content cannot be null");
        }

        // Support both old format (questions) and new format (parts)
        if (contentJson.containsKey("questions")) {
            validateQuestionsFormat(contentJson);
        } else if (contentJson.containsKey("parts")) {
            validatePartsFormat(contentJson);
        } else {
            throw new BadRequestException("Content must contain either 'questions' or 'parts' field");
        }
    }

    /**
     * Validate questions format (old format)
     */
    private void validateQuestionsFormat(Map<String, Object> contentJson) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> questions = (List<Map<String, Object>>) contentJson.get("questions");

        if (questions == null || questions.isEmpty()) {
            throw new BadRequestException("Exam must contain at least one question");
        }

        for (int i = 0; i < questions.size(); i++) {
            Map<String, Object> question = questions.get(i);
            validateSingleQuestion(question, "Question " + (i + 1));
        }
    }

    /**
     * Validate parts format (new format)
     */
    private void validatePartsFormat(Map<String, Object> contentJson) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> parts = (List<Map<String, Object>>) contentJson.get("parts");

        if (parts == null || parts.isEmpty()) {
            throw new BadRequestException("Exam must contain at least one part");
        }

        for (int partIndex = 0; partIndex < parts.size(); partIndex++) {
            Map<String, Object> part = parts.get(partIndex);
            String partName = "Part " + (partIndex + 1);

            if (!part.containsKey("questions")) {
                throw new BadRequestException(partName + " must contain 'questions' field");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> questions = (List<Map<String, Object>>) part.get("questions");

            if (questions == null || questions.isEmpty()) {
                throw new BadRequestException(partName + " must contain at least one question");
            }

            for (int qIndex = 0; qIndex < questions.size(); qIndex++) {
                Map<String, Object> question = questions.get(qIndex);
                validateSingleQuestion(question, partName + " Question " + (qIndex + 1));
            }
        }
    }

    /**
     * Validate single question structure
     */
    private void validateSingleQuestion(Map<String, Object> question, String questionLabel) {
        if (!question.containsKey("id")) {
            throw new BadRequestException(questionLabel + " must have an 'id' field");
        }

        if (!question.containsKey("question")) {
            throw new BadRequestException(questionLabel + " must have a 'question' field");
        }

        // Check for answer field (support multiple formats)
        boolean hasAnswer = question.containsKey("answer") ||
                           question.containsKey("correctAnswer") ||
                           question.containsKey("statements"); // For true/false questions

        if (!hasAnswer) {
            throw new BadRequestException(questionLabel + " must have an 'answer', 'correctAnswer', or 'statements' field");
        }
    }

    /**
     * Get correct answer from question
     */
    public String getCorrectAnswer(Map<String, Object> question) {
        if (question.containsKey("correctAnswer")) {
            return String.valueOf(question.get("correctAnswer"));
        }
        if (question.containsKey("answer")) {
            return String.valueOf(question.get("answer"));
        }
        return null;
    }
}
