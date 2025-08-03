package com.BE.service.implementService;

import com.BE.enums.DifficultyLevel;
import com.BE.enums.QuestionType;
import com.BE.enums.QuestionBankVisibility;
import com.BE.exception.BadRequestException;
import com.BE.exception.ResourceNotFoundException;
import com.BE.mapper.QuestionBankMapper;
import com.BE.model.entity.QuestionBank;
import com.BE.model.request.CreateQuestionBankRequest;
import com.BE.model.request.UpdateQuestionBankRequest;
import com.BE.model.response.QuestionBankResponse;
import com.BE.repository.QuestionBankRepository;
import com.BE.service.interfaceService.IQuestionBankService;
import com.BE.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionBankServiceImpl implements IQuestionBankService {

    private final QuestionBankRepository questionBankRepository;
    private final QuestionBankMapper questionBankMapper;
    private final AccountUtils accountUtils;

    @Override
    public QuestionBankResponse createQuestionBank(CreateQuestionBankRequest request) {
        try {
            // VALIDATION DISABLED FOR TESTING
            // questionBankUtils.validateQuestionContent(request.getQuestionContent(), request.getQuestionType());

            UUID currentUserId = accountUtils.getCurrentUserId();
            QuestionBank questionBank = questionBankMapper.toEntity(request, currentUserId);

            // Set visibility based on user role
            boolean isStaff = accountUtils.isCurrentUserStaff();
            questionBank.setVisibility(QuestionBankVisibility.getByUserRole(isStaff));

            QuestionBank savedQuestionBank = questionBankRepository.save(questionBank);
            log.info("Created question bank {} with visibility {} by user {}",
                    savedQuestionBank.getId(), savedQuestionBank.getVisibility(), currentUserId);

            return questionBankMapper.toResponse(savedQuestionBank);

        } catch (Exception e) {
            log.error("Error creating question bank: {}", e.getMessage(), e);
            throw new BadRequestException("Lỗi khi tạo câu hỏi: " + e.getMessage());
        }
    }



    @Override
    public QuestionBankResponse getQuestionBankById(Long id) {
        try {
            UUID currentUserId = accountUtils.getCurrentUserId();
            QuestionBank questionBank = questionBankRepository.findByIdAndAccessible(id, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Question bank not found with id: " + id + " or you don't have permission to access it"));
            log.info("Retrieved question bank {} for user {}", id, currentUserId);
            return questionBankMapper.toResponse(questionBank);
        } catch (Exception e) {
            log.error("Error getting question bank by id {}: {}", id, e.getMessage(), e);
            throw new BadRequestException("Lỗi khi lấy thông tin câu hỏi: " + e.getMessage());
        }
    }

    @Override
    public QuestionBankResponse updateQuestionBank(Long id, UpdateQuestionBankRequest request) {
        try {
            UUID currentUserId = accountUtils.getCurrentUserId();
            QuestionBank questionBank = questionBankRepository.findByIdAndCreatedBy(id, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Question bank not found with id: " + id + " or you don't have permission to access it"));

            // Update fields if provided
            if (request.getLessonId() != null) {
                questionBank.setLessonId(request.getLessonId());
            }
            if (request.getQuestionType() != null) {
                questionBank.setQuestionType(request.getQuestionType());
            }
            if (request.getDifficultyLevel() != null) {
                questionBank.setDifficultyLevel(request.getDifficultyLevel());
            }
            if (request.getQuestionContent() != null) {
                // VALIDATION DISABLED FOR TESTING
                // questionBankUtils.validateQuestionContent(request.getQuestionContent(), request.getQuestionType());
                questionBank.setQuestionContent(request.getQuestionContent());
            }
            if (request.getExplanation() != null) {
                questionBank.setExplanation(request.getExplanation());
            }
            if (request.getReferenceSource() != null) {
                questionBank.setReferenceSource(request.getReferenceSource());
            }
            // Note: Visibility cannot be updated after creation to maintain data integrity

            questionBank.setUpdatedBy(currentUserId);
            QuestionBank savedQuestionBank = questionBankRepository.save(questionBank);
            log.info("Updated question bank {} by user {}", id, currentUserId);
            return questionBankMapper.toResponse(savedQuestionBank);
        } catch (Exception e) {
            log.error("Error updating question bank {}: {}", id, e.getMessage(), e);
            throw new BadRequestException("Lỗi khi cập nhật câu hỏi: " + e.getMessage());
        }
    }

    @Override
    public void deleteQuestionBank(Long id) {
        try {
            UUID currentUserId = accountUtils.getCurrentUserId();
            QuestionBank questionBank = questionBankRepository.findByIdAndCreatedBy(id, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Question bank not found with id: " + id + " or you don't have permission to access it"));

            // Hard delete the question bank
            questionBankRepository.delete(questionBank);
            log.info("Deleted question bank {} by user {}", id, currentUserId);
        } catch (Exception e) {
            log.error("Error deleting question bank {}: {}", id, e.getMessage(), e);
            throw new BadRequestException("Lỗi khi xóa câu hỏi: " + e.getMessage());
        }
    }









    @Override
    public QuestionBankStatistics getQuestionBankStatistics() {
        try {
            UUID currentUserId = accountUtils.getCurrentUserId();

            // Get all question banks accessible to current user (public + own private)
            List<QuestionBank> allQuestionBanks = questionBankRepository.findByFilters(currentUserId, null, null, null);

            // Calculate basic statistics
            Long totalQuestions = (long) allQuestionBanks.size();
            Long availableQuestions = allQuestionBanks.stream()
                    .filter(QuestionBank::isAvailable)
                    .count();
            // Group by question type
            Map<QuestionType, Long> questionsByType = allQuestionBanks.stream()
                    .filter(QuestionBank::isAvailable)
                    .collect(Collectors.groupingBy(
                            QuestionBank::getQuestionType,
                            Collectors.counting()
                    ));

            // Group by difficulty level
            Map<DifficultyLevel, Long> questionsByDifficulty = allQuestionBanks.stream()
                    .filter(QuestionBank::isAvailable)
                    .collect(Collectors.groupingBy(
                            QuestionBank::getDifficultyLevel,
                            Collectors.counting()
                    ));

            // Group by lesson
            Map<Long, Long> questionsByLesson = allQuestionBanks.stream()
                    .filter(QuestionBank::isAvailable)
                    .collect(Collectors.groupingBy(
                            QuestionBank::getLessonId,
                            Collectors.counting()
                    ));

            log.info("Generated statistics for user {}: total={}, available={}",
                    currentUserId, totalQuestions, availableQuestions);

            return new QuestionBankStatistics(
                    totalQuestions,
                    availableQuestions,
                    questionsByType,
                    questionsByDifficulty,
                    questionsByLesson
            );
        } catch (Exception e) {
            log.error("Error getting question bank statistics: {}", e.getMessage(), e);
            throw new BadRequestException("Lỗi khi lấy thống kê câu hỏi: " + e.getMessage());
        }
    }

    @Override
    public List<QuestionBankResponse> getQuestionBanksByFilters(Long lessonId, List<QuestionType> questionTypes, List<DifficultyLevel> difficultyLevels) {
        try {
            UUID currentUserId = accountUtils.getCurrentUserId();
            List<QuestionBank> questionBanks = questionBankRepository.findByMultipleFilters(currentUserId, lessonId, questionTypes, difficultyLevels);
            if (lessonId == null && (questionTypes == null || questionTypes.isEmpty()) && (difficultyLevels == null || difficultyLevels.isEmpty())) {
                log.info("Found {} question banks (all accessible) for user {}", questionBanks.size(), currentUserId);
            } else {
                log.info("Found {} question banks with filters (lesson: {}, types: {}, difficulties: {}) for user {}",
                        questionBanks.size(), lessonId, questionTypes, difficultyLevels, currentUserId);
            }
            return questionBanks.stream()
                    .map(questionBankMapper::toResponse)
                    .toList();
        } catch (Exception e) {
            log.error("Error getting question banks by multiple filters: {}", e.getMessage(), e);
            throw new BadRequestException("Lỗi khi lọc câu hỏi: " + e.getMessage());
        }
    }

    @Override
    public Page<QuestionBankResponse> getQuestionBanksByFilters(Long lessonId, List<QuestionType> questionTypes, List<DifficultyLevel> difficultyLevels, Pageable pageable) {
        try {
            UUID currentUserId = accountUtils.getCurrentUserId();
            Page<QuestionBank> questionBanks = questionBankRepository.findByMultipleFilters(currentUserId, lessonId, questionTypes, difficultyLevels, pageable);
            if (lessonId == null && (questionTypes == null || questionTypes.isEmpty()) && (difficultyLevels == null || difficultyLevels.isEmpty())) {
                log.info("Found {} question banks (all accessible) for user {} (page {}, size {})",
                        questionBanks.getTotalElements(), currentUserId, pageable.getPageNumber(), pageable.getPageSize());
            } else {
                log.info("Found {} question banks with filters (lesson: {}, types: {}, difficulties: {}) for user {} (page {}, size {})",
                        questionBanks.getTotalElements(), lessonId, questionTypes, difficultyLevels, currentUserId,
                        pageable.getPageNumber(), pageable.getPageSize());
            }
            return questionBanks.map(questionBankMapper::toResponse);
        } catch (Exception e) {
            log.error("Error getting question banks by multiple filters with pagination: {}", e.getMessage(), e);
            throw new BadRequestException("Lỗi khi lọc câu hỏi: " + e.getMessage());
        }
    }


}
