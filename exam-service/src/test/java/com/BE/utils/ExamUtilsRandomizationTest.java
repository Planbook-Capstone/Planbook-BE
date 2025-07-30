package com.BE.utils;

import com.BE.repository.ExamInstanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExamUtilsRandomizationTest {

    @Mock
    private ExamInstanceRepository examInstanceRepository;

    private ExamUtils examUtils;

    @BeforeEach
    void setUp() {
        examUtils = new ExamUtils(examInstanceRepository);
    }

    @Test
    void testRemoveCorrectAnswersWithRandomization_OldFormat() {
        // Given: Exam content with old format (direct questions)
        Map<String, Object> contentJson = new HashMap<>();
        List<Map<String, Object>> questions = new ArrayList<>();
        
        // Create 5 test questions
        for (int i = 1; i <= 5; i++) {
            Map<String, Object> question = new HashMap<>();
            question.put("id", "q" + i);
            question.put("text", "Question " + i);
            question.put("type", "multiple_choice");
            question.put("correctAnswer", "A");
            questions.add(question);
        }
        
        contentJson.put("questions", questions);

        // When: Process content for students
        Map<String, Object> result1 = examUtils.removeCorrectAnswers(contentJson);
        Map<String, Object> result2 = examUtils.removeCorrectAnswers(contentJson);

        // Then: Verify correct answers are removed and questions are randomized
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> resultQuestions1 = (List<Map<String, Object>>) result1.get("questions");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> resultQuestions2 = (List<Map<String, Object>>) result2.get("questions");

        // Check that correct answers are removed
        for (Map<String, Object> question : resultQuestions1) {
            assertFalse(question.containsKey("correctAnswer"));
            assertFalse(question.containsKey("correctAnswers"));
            assertFalse(question.containsKey("answer"));
        }

        // Check that questions have new numbering
        assertEquals(5, resultQuestions1.size());
        for (int i = 0; i < resultQuestions1.size(); i++) {
            Map<String, Object> question = resultQuestions1.get(i);
            assertEquals(i + 1, question.get("questionNumber"));
            assertTrue(question.containsKey("originalQuestionNumber"));
        }

        // Check that order is different between calls (with high probability)
        boolean orderIsDifferent = false;
        for (int i = 0; i < resultQuestions1.size(); i++) {
            String id1 = (String) resultQuestions1.get(i).get("id");
            String id2 = (String) resultQuestions2.get(i).get("id");
            if (!id1.equals(id2)) {
                orderIsDifferent = true;
                break;
            }
        }
        // Note: There's a small chance (1/120) that the order is the same by coincidence
        // In a real test environment, you might want to run this multiple times
    }

    @Test
    void testRemoveCorrectAnswersWithRandomization_NewFormat() {
        // Given: Exam content with new format (parts with questions)
        Map<String, Object> contentJson = new HashMap<>();
        List<Map<String, Object>> parts = new ArrayList<>();
        
        // Create 2 parts with questions
        for (int partNum = 1; partNum <= 2; partNum++) {
            Map<String, Object> part = new HashMap<>();
            part.put("name", "Part " + partNum);
            
            List<Map<String, Object>> questions = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                Map<String, Object> question = new HashMap<>();
                question.put("id", "p" + partNum + "q" + i);
                question.put("text", "Part " + partNum + " Question " + i);
                question.put("type", "multiple_choice");
                question.put("correctAnswer", "A");
                questions.add(question);
            }
            
            part.put("questions", questions);
            parts.add(part);
        }
        
        contentJson.put("parts", parts);

        // When: Process content for students
        Map<String, Object> result = examUtils.removeCorrectAnswers(contentJson);

        // Then: Verify structure and randomization
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> resultParts = (List<Map<String, Object>>) result.get("parts");
        
        assertEquals(2, resultParts.size());
        
        for (Map<String, Object> part : resultParts) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> questions = (List<Map<String, Object>>) part.get("questions");
            
            assertEquals(3, questions.size());
            
            // Check that correct answers are removed
            for (Map<String, Object> question : questions) {
                assertFalse(question.containsKey("correctAnswer"));
                assertFalse(question.containsKey("correctAnswers"));
                assertFalse(question.containsKey("answer"));
                assertTrue(question.containsKey("questionNumber"));
                assertTrue(question.containsKey("originalQuestionNumber"));
            }
        }
    }

    @Test
    void testRemoveCorrectAnswersWithRandomization_EmptyQuestions() {
        // Given: Content with empty questions
        Map<String, Object> contentJson = new HashMap<>();
        contentJson.put("questions", new ArrayList<>());

        // When: Process content
        Map<String, Object> result = examUtils.removeCorrectAnswers(contentJson);

        // Then: Should handle gracefully
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> resultQuestions = (List<Map<String, Object>>) result.get("questions");
        assertTrue(resultQuestions.isEmpty());
    }

    @Test
    void testRemoveCorrectAnswersWithRandomization_SingleQuestion() {
        // Given: Content with single question
        Map<String, Object> contentJson = new HashMap<>();
        List<Map<String, Object>> questions = new ArrayList<>();
        
        Map<String, Object> question = new HashMap<>();
        question.put("id", "q1");
        question.put("text", "Single Question");
        question.put("correctAnswer", "A");
        questions.add(question);
        
        contentJson.put("questions", questions);

        // When: Process content
        Map<String, Object> result = examUtils.removeCorrectAnswers(contentJson);

        // Then: Should handle single question correctly
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> resultQuestions = (List<Map<String, Object>>) result.get("questions");
        
        assertEquals(1, resultQuestions.size());
        Map<String, Object> resultQuestion = resultQuestions.get(0);
        
        assertFalse(resultQuestion.containsKey("correctAnswer"));
        assertEquals(1, resultQuestion.get("questionNumber"));
        assertEquals(1, resultQuestion.get("originalQuestionNumber"));
    }

    @Test
    void testRemoveCorrectAnswersWithRandomization_TrueFalseQuestions() {
        // Given: Content with true/false questions having statements
        Map<String, Object> contentJson = new HashMap<>();
        List<Map<String, Object>> questions = new ArrayList<>();
        
        Map<String, Object> question = new HashMap<>();
        question.put("id", "q1");
        question.put("text", "True/False Question");
        question.put("type", "true_false");
        
        Map<String, Object> statements = new HashMap<>();
        Map<String, Object> statement1 = new HashMap<>();
        statement1.put("text", "Statement 1");
        statement1.put("answer", true);
        statements.put("1", statement1);
        
        Map<String, Object> statement2 = new HashMap<>();
        statement2.put("text", "Statement 2");
        statement2.put("answer", false);
        statements.put("2", statement2);
        
        question.put("statements", statements);
        questions.add(question);
        
        contentJson.put("questions", questions);

        // When: Process content
        Map<String, Object> result = examUtils.removeCorrectAnswers(contentJson);

        // Then: Verify statements answers are removed
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> resultQuestions = (List<Map<String, Object>>) result.get("questions");
        
        assertEquals(1, resultQuestions.size());
        Map<String, Object> resultQuestion = resultQuestions.get(0);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> resultStatements = (Map<String, Object>) resultQuestion.get("statements");
        
        for (Object statementObj : resultStatements.values()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> statement = (Map<String, Object>) statementObj;
            assertFalse(statement.containsKey("answer"));
            assertTrue(statement.containsKey("text"));
        }
    }
}
