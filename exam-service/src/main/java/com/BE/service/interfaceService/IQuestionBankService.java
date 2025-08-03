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
     * Delete question bank
     * @param questionBankId The question bank ID
     */
    void deleteQuestionBank(Long questionBankId);


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









}
