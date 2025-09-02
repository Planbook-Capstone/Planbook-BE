package com.BE.service.implementServices;

import com.BE.exception.ResourceNotFoundException;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.model.entity.AnswerSheetKey;
import com.BE.model.entity.GradingSession;
import com.BE.model.entity.StudentSubmission;
import com.BE.model.request.StudentSubmissionRequest;
import com.BE.repository.AnswerSheetKeyRepository;
import com.BE.repository.GradingSessionRepository;
import com.BE.repository.StudentSubmissionRepository;
import com.BE.service.interfaceServices.GradingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GradingServiceImpl implements GradingService {

    private final AnswerSheetKeyRepository answerSheetKeyRepository;
    private final GradingSessionRepository gradingSessionRepository;
    private final StudentSubmissionRepository studentSubmissionRepository;

    @Override
    public StudentSubmission gradeSubmission(StudentSubmissionRequest request) {
        GradingSession gradingSession = gradingSessionRepository.findById(request.getGradingSessionId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phiên chấm điểm với ID: " + request.getGradingSessionId()));

        AnswerSheetKey answerSheetKey = answerSheetKeyRepository
                .findByGradingSessionIdAndCode(request.getGradingSessionId(), request.getExamCode())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy mã đề với mã: " + request.getExamCode()));

        JsonNode answerKeyJson = answerSheetKey.getAnswerJson();
        JsonNode studentAnswersJson = request.getStudentAnswerJson();
        JsonNode sectionConfigJson = gradingSession.getSectionConfigJson();

        Map<String, String> correctAnswers = parseCorrectAnswers(answerKeyJson);
        Map<Integer, JsonNode> sectionConfigs = parseSectionConfigs(sectionConfigJson);

        float totalScore = 0f;
        int totalCorrect = 0;

        for (JsonNode section : studentAnswersJson.path("parts")) {
            int sectionOrder = section.path("sectionOrder").asInt();
            JsonNode config = sectionConfigs.get(sectionOrder);
            if (config == null) continue; // Skip sections without configuration

            String sectionType = config.path("sectionType").asText();

            for (JsonNode question : section.path("answers")) {
                String questionNumber = question.path("questionNumber").asText();
                String studentAnswer = question.path("studentAnswer").asText();
                String correctAnswer = correctAnswers.get(questionNumber);

                boolean isCorrect = correctAnswer != null && correctAnswer.equalsIgnoreCase(studentAnswer);
                float points = 0.0f;

                if (isCorrect) {
                    totalCorrect++;
                    if ("MULTIPLE_CHOICE".equals(sectionType)) {
                        points = (float) config.path("pointsPerQuestion").asDouble(0.0);
                    } else if ("TRUE_FALSE".equals(sectionType)) {
                        // For TRUE_FALSE, the points are determined by matching the string (e.g., "Đúng 3 Ý") to the rule.
                        JsonNode ruleNode = config.path("rule");
                        // This part assumes the student's answer format provides a key to the rule map.
                        // E.g., if studentAnswer is "3", it looks for a "3" key in the rule.
                        // A more robust implementation might be needed depending on the exact format of `studentAnswer`.
                        String correctItemsCount = studentAnswer.replaceAll("[^0-9]", "");
                        if (!correctItemsCount.isEmpty()) {
                           points = (float) ruleNode.path(correctItemsCount).asDouble(0.0);
                        }
                    }
                }
                totalScore += points;

                ((ObjectNode) question).put("correctAnswer", correctAnswer);
                ((ObjectNode) question).put("isCorrect", isCorrect);
                ((ObjectNode) question).put("pointsAwarded", points);
            }
        }

        StudentSubmission submission = StudentSubmission.builder()
                .gradingSession(gradingSession)
                .answerSheetKey(answerSheetKey)
                .studentCode(request.getStudentCode())
                .examCode(request.getExamCode())
                .imageBase64(request.getImageBase64())
                .score(totalScore)
                .totalCorrect(totalCorrect)
                .submittedAt(LocalDateTime.now())
                .studentAnswerJson(studentAnswersJson)
                .build();

        return studentSubmissionRepository.save(submission);
    }

    private Map<String, String> parseCorrectAnswers(JsonNode answerKeyJson) {
        Map<String, String> correctAnswers = new HashMap<>();
        if (answerKeyJson != null && answerKeyJson.has("parts")) {
            for (JsonNode section : answerKeyJson.path("parts")) {
                for (JsonNode question : section.path("questions")) {
                    correctAnswers.put(
                            question.path("questionNumber").asText(),
                            question.path("correctOption").asText()
                    );
                }
            }
        }
        return correctAnswers;
    }

    private Map<Integer, JsonNode> parseSectionConfigs(JsonNode sectionConfigJson) {
        Map<Integer, JsonNode> sectionConfigs = new HashMap<>();
        if (sectionConfigJson != null && sectionConfigJson.isArray()) {
            for (JsonNode config : sectionConfigJson) {
                sectionConfigs.put(config.path("sectionOrder").asInt(), config);
            }
        }
        return sectionConfigs;
    }
}
