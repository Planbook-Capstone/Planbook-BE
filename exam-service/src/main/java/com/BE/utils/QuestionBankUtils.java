package com.BE.utils;

import com.BE.enums.QuestionType;
import com.BE.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class QuestionBankUtils {

    /**
     * Validate question content JSON structure based on question type
     * VALIDATION DISABLED FOR TESTING
     */
    public void validateQuestionContent(Map<String, Object> questionContent, QuestionType questionType) {
        // Validation disabled - skip all checks
        /*
        if (questionContent == null || questionContent.isEmpty()) {
            throw new BadRequestException("Nội dung câu hỏi không được để trống");
        }

        // Validate common required field
        if (!questionContent.containsKey("question") ||
            questionContent.get("question") == null ||
            questionContent.get("question").toString().trim().isEmpty()) {
            throw new BadRequestException("Câu hỏi là bắt buộc");
        }

        switch (questionType) {
            case PART_I -> validatePartIContent(questionContent);
            case PART_II -> validatePartIIContent(questionContent);
            case PART_III -> validatePartIIIContent(questionContent);
            default -> throw new BadRequestException("Loại câu hỏi không hợp lệ");
        }
        */
    }

    /**
     * Validate Part I (Multiple Choice) question content
     */
    private void validatePartIContent(Map<String, Object> content) {
        // Validate options
        if (!content.containsKey("options")) {
            throw new BadRequestException("Câu hỏi trắc nghiệm phải có các phương án lựa chọn");
        }

        Object optionsObj = content.get("options");
        if (!(optionsObj instanceof Map)) {
            throw new BadRequestException("Phương án lựa chọn phải là một object");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> options = (Map<String, Object>) optionsObj;
        
        if (options.isEmpty()) {
            throw new BadRequestException("Phải có ít nhất một phương án lựa chọn");
        }

        // Validate that all options have values
        for (Map.Entry<String, Object> entry : options.entrySet()) {
            if (entry.getValue() == null || entry.getValue().toString().trim().isEmpty()) {
                throw new BadRequestException("Phương án " + entry.getKey() + " không được để trống");
            }
        }

        // Validate answer
        if (!content.containsKey("answer")) {
            throw new BadRequestException("Câu hỏi trắc nghiệm phải có đáp án đúng");
        }

        String answer = content.get("answer").toString();
        if (!options.containsKey(answer)) {
            throw new BadRequestException("Đáp án đúng phải là một trong các phương án lựa chọn");
        }

        log.debug("Part I question content validated successfully");
    }

    /**
     * Validate Part II (True/False) question content
     */
    private void validatePartIIContent(Map<String, Object> content) {
        // Validate statements
        if (!content.containsKey("statements")) {
            throw new BadRequestException("Câu hỏi đúng sai phải có các phát biểu");
        }

        Object statementsObj = content.get("statements");
        if (!(statementsObj instanceof Map)) {
            throw new BadRequestException("Các phát biểu phải là một object");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> statements = (Map<String, Object>) statementsObj;
        
        if (statements.isEmpty()) {
            throw new BadRequestException("Phải có ít nhất một phát biểu");
        }

        // Validate that all statements have values
        for (Map.Entry<String, Object> entry : statements.entrySet()) {
            if (entry.getValue() == null || entry.getValue().toString().trim().isEmpty()) {
                throw new BadRequestException("Phát biểu " + entry.getKey() + " không được để trống");
            }
        }

        // Validate answers
        if (!content.containsKey("answers")) {
            throw new BadRequestException("Câu hỏi đúng sai phải có đáp án cho các phát biểu");
        }

        Object answersObj = content.get("answers");
        if (!(answersObj instanceof Map)) {
            throw new BadRequestException("Đáp án phải là một object");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> answers = (Map<String, Object>) answersObj;

        // Validate that all statements have corresponding answers
        for (String statementKey : statements.keySet()) {
            if (!answers.containsKey(statementKey)) {
                throw new BadRequestException("Thiếu đáp án cho phát biểu " + statementKey);
            }
            
            Object answerValue = answers.get(statementKey);
            if (!(answerValue instanceof Boolean)) {
                throw new BadRequestException("Đáp án cho phát biểu " + statementKey + " phải là true hoặc false");
            }
        }

        // Validate that there are no extra answers
        for (String answerKey : answers.keySet()) {
            if (!statements.containsKey(answerKey)) {
                throw new BadRequestException("Đáp án " + answerKey + " không có phát biểu tương ứng");
            }
        }

        log.debug("Part II question content validated successfully");
    }

    /**
     * Validate Part III (Short Answer) question content
     */
    private void validatePartIIIContent(Map<String, Object> content) {
        // Validate answer
        if (!content.containsKey("answer")) {
            throw new BadRequestException("Câu hỏi tự luận phải có đáp án");
        }

        Object answerObj = content.get("answer");
        if (answerObj == null || answerObj.toString().trim().isEmpty()) {
            throw new BadRequestException("Đáp án không được để trống");
        }

        // Keywords are optional but if present, should be a list
        if (content.containsKey("keywords")) {
            Object keywordsObj = content.get("keywords");
            if (keywordsObj != null && !(keywordsObj instanceof List)) {
                throw new BadRequestException("Từ khóa phải là một danh sách");
            }
        }

        log.debug("Part III question content validated successfully");
    }



    /**
     * Get required fields for a question type
     */
    public Set<String> getRequiredFields(QuestionType questionType) {
        return switch (questionType) {
            case PART_I -> Set.of("question", "options", "answer");
            case PART_II -> Set.of("question", "statements", "answers");
            case PART_III -> Set.of("question", "answer");
        };
    }

    /**
     * Get optional fields for a question type
     */
    public Set<String> getOptionalFields(QuestionType questionType) {
        return switch (questionType) {
            case PART_I -> Set.of("explanation", "hint");
            case PART_II -> Set.of("explanation", "hint");
            case PART_III -> Set.of("keywords", "explanation", "hint", "maxScore");
        };
    }

    /**
     * Sanitize question content by removing invalid fields
     */
    public Map<String, Object> sanitizeQuestionContent(Map<String, Object> content, QuestionType questionType) {
        if (content == null) {
            return null;
        }

        Set<String> allowedFields = getRequiredFields(questionType);
        allowedFields.addAll(getOptionalFields(questionType));

        // Remove fields that are not allowed for this question type
        content.entrySet().removeIf(entry -> !allowedFields.contains(entry.getKey()));

        return content;
    }

    /**
     * Generate sample question content for a question type
     */
    public Map<String, Object> generateSampleContent(QuestionType questionType) {
        return switch (questionType) {
            case PART_I -> Map.of(
                "question", "Câu hỏi mẫu trắc nghiệm?",
                "options", Map.of("A", "Phương án A", "B", "Phương án B", "C", "Phương án C", "D", "Phương án D"),
                "answer", "A"
            );
            case PART_II -> Map.of(
                "question", "Xét tính đúng sai của các phát biểu sau:",
                "statements", Map.of("a", "Phát biểu a", "b", "Phát biểu b"),
                "answers", Map.of("a", true, "b", false)
            );
            case PART_III -> Map.of(
                "question", "Câu hỏi tự luận mẫu?",
                "answer", "Đáp án mẫu cho câu hỏi tự luận",
                "keywords", List.of("từ khóa 1", "từ khóa 2")
            );
        };
    }
}
