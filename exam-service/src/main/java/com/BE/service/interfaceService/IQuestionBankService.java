package com.BE.service.interfaceService;

import com.BE.enums.DifficultyLevel;
import com.BE.enums.QuestionType;
import com.BE.model.request.CreateQuestionBankRequest;
import com.BE.model.request.UpdateQuestionBankRequest;
import com.BE.model.response.QuestionBankResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IQuestionBankService {

    /**
     * Create a new question bank entry
     * @param request The question bank creation request
     * @return Created question bank response
     */
    QuestionBankResponse createQuestionBank(CreateQuestionBankRequest request);

    /**
     * Get all question banks created by the current user
     * @return List of question bank responses
     */
    List<QuestionBankResponse> getQuestionBanksByCurrentUser();

    /**
     * Get question banks with pagination by current user
     * @param pageable Pagination information
     * @return Page of question bank responses
     */
    Page<QuestionBankResponse> getQuestionBanksByCurrentUser(Pageable pageable);

    /**
     * Get question bank by ID
     * @param questionBankId The question bank ID
     * @return Question bank response
     */
    QuestionBankResponse getQuestionBankById(Long questionBankId);

    /**
     * Update question bank
     * @param questionBankId The question bank ID
     * @param request The update request
     * @return Updated question bank response
     */
    QuestionBankResponse updateQuestionBank(Long questionBankId, UpdateQuestionBankRequest request);

    /**
     * Delete question bank (soft delete by setting isActive to false)
     * @param questionBankId The question bank ID
     */
    void deleteQuestionBank(Long questionBankId);

    /**
     * Get question banks by lesson ID
     * @param lessonId The lesson ID
     * @return List of question bank responses
     */
    List<QuestionBankResponse> getQuestionBanksByLessonId(Long lessonId);

    /**
     * Get question banks by question type
     * @param questionType The question type
     * @return List of question bank responses
     */
    List<QuestionBankResponse> getQuestionBanksByQuestionType(QuestionType questionType);

    /**
     * Get question banks by difficulty level
     * @param difficultyLevel The difficulty level
     * @return List of question bank responses
     */
    List<QuestionBankResponse> getQuestionBanksByDifficultyLevel(DifficultyLevel difficultyLevel);

    /**
     * Get question banks with multiple filters
     * @param lessonId The lesson ID (optional)
     * @param questionType The question type (optional)
     * @param difficultyLevel The difficulty level (optional)
     * @return List of question bank responses
     */
    List<QuestionBankResponse> getQuestionBanksByFilters(Long lessonId, QuestionType questionType,
                                                        DifficultyLevel difficultyLevel);

    /**
     * Get question banks with multiple filters and pagination
     * @param lessonId The lesson ID (optional)
     * @param questionType The question type (optional)
     * @param difficultyLevel The difficulty level (optional)
     * @param pageable Pagination information
     * @return Page of question bank responses
     */
    Page<QuestionBankResponse> getQuestionBanksByFilters(Long lessonId, QuestionType questionType,
                                                        DifficultyLevel difficultyLevel,
                                                        Pageable pageable);

    /**
     * Get question banks with multiple filters (supports multiple types and difficulties)
     * @param lessonId The lesson ID (optional)
     * @param questionTypes List of question types (optional)
     * @param difficultyLevels List of difficulty levels (optional)
     * @return List of question bank responses
     */
    List<QuestionBankResponse> getQuestionBanksByFilters(Long lessonId, List<QuestionType> questionTypes,
                                                        List<DifficultyLevel> difficultyLevels);

    /**
     * Get question banks with multiple filters and pagination (supports multiple types and difficulties)
     * @param lessonId The lesson ID (optional)
     * @param questionTypes List of question types (optional)
     * @param difficultyLevels List of difficulty levels (optional)
     * @param pageable Pagination information
     * @return Page of question bank responses
     */
    Page<QuestionBankResponse> getQuestionBanksByFilters(Long lessonId, List<QuestionType> questionTypes,
                                                        List<DifficultyLevel> difficultyLevels,
                                                        Pageable pageable);



    /**
     * Search question banks by keyword
     * @param keyword The search keyword
     * @return List of question bank responses
     */
    List<QuestionBankResponse> searchQuestionBanks(String keyword);

    /**
     * Search question banks by keyword with pagination
     * @param keyword The search keyword
     * @param pageable Pagination information
     * @return Page of question bank responses
     */
    Page<QuestionBankResponse> searchQuestionBanks(String keyword, Pageable pageable);





    /**
     * Get question bank statistics for current user
     * @return Statistics object containing counts by type, difficulty, etc.
     */
    QuestionBankStatistics getQuestionBankStatistics();



    /**
     * Generate exam template from selected question banks
     * @param questionBankIds List of question bank IDs
     * @return Template in parts-based format
     */
    java.util.Map<String, Object> generateExamTemplate(java.util.List<Long> questionBankIds);

    /**
     * Statistics inner class
     */
    class QuestionBankStatistics {
        private Long totalQuestions;
        private Long activeQuestions;
        private Long totalUsageCount;
        private java.util.Map<QuestionType, Long> questionsByType;
        private java.util.Map<DifficultyLevel, Long> questionsByDifficulty;
        private java.util.Map<Long, Long> questionsByLesson;

        // Constructors, getters, setters
        public QuestionBankStatistics() {}

        public QuestionBankStatistics(Long totalQuestions, Long activeQuestions, Long totalUsageCount,
                                    java.util.Map<QuestionType, Long> questionsByType,
                                    java.util.Map<DifficultyLevel, Long> questionsByDifficulty,
                                    java.util.Map<Long, Long> questionsByLesson) {
            this.totalQuestions = totalQuestions;
            this.activeQuestions = activeQuestions;
            this.totalUsageCount = totalUsageCount;
            this.questionsByType = questionsByType;
            this.questionsByDifficulty = questionsByDifficulty;
            this.questionsByLesson = questionsByLesson;
        }

        // Getters and setters
        public Long getTotalQuestions() { return totalQuestions; }
        public void setTotalQuestions(Long totalQuestions) { this.totalQuestions = totalQuestions; }

        public Long getActiveQuestions() { return activeQuestions; }
        public void setActiveQuestions(Long activeQuestions) { this.activeQuestions = activeQuestions; }

        public Long getTotalUsageCount() { return totalUsageCount; }
        public void setTotalUsageCount(Long totalUsageCount) { this.totalUsageCount = totalUsageCount; }

        public java.util.Map<QuestionType, Long> getQuestionsByType() { return questionsByType; }
        public void setQuestionsByType(java.util.Map<QuestionType, Long> questionsByType) { this.questionsByType = questionsByType; }

        public java.util.Map<DifficultyLevel, Long> getQuestionsByDifficulty() { return questionsByDifficulty; }
        public void setQuestionsByDifficulty(java.util.Map<DifficultyLevel, Long> questionsByDifficulty) { this.questionsByDifficulty = questionsByDifficulty; }

        public java.util.Map<Long, Long> getQuestionsByLesson() { return questionsByLesson; }
        public void setQuestionsByLesson(java.util.Map<Long, Long> questionsByLesson) { this.questionsByLesson = questionsByLesson; }
    }
}
