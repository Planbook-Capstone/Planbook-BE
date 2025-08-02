package com.BE.service.implementService;

import com.BE.enums.DifficultyLevel;
import com.BE.enums.QuestionType;
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

            QuestionBank savedQuestionBank = questionBankRepository.save(questionBank);
            log.info("Created question bank {} by user {}", savedQuestionBank.getId(), currentUserId);

            return questionBankMapper.toResponse(savedQuestionBank);

        } catch (Exception e) {
            log.error("Error creating question bank: {}", e.getMessage(), e);
            throw new BadRequestException("Lỗi khi tạo câu hỏi: " + e.getMessage());
        }
    }

    @Override
    public List<QuestionBankResponse> getQuestionBanksByCurrentUser() {
        try {
            UUID currentUserId = accountUtils.getCurrentUserId();
            List<QuestionBank> questionBanks = questionBankRepository.findByCreatedByOrderByCreatedAtDesc(currentUserId);
            log.info("Found {} question banks for user {}", questionBanks.size(), currentUserId);
            return questionBanks.stream()
                    .map(questionBankMapper::toResponse)
                    .toList();
        } catch (Exception e) {
            log.error("Error getting question banks for current user: {}", e.getMessage(), e);
            throw new BadRequestException("Lỗi khi lấy danh sách câu hỏi: " + e.getMessage());
        }
    }

    @Override
    public Page<QuestionBankResponse> getQuestionBanksByCurrentUser(Pageable pageable) {
        try {
            UUID currentUserId = accountUtils.getCurrentUserId();
            Page<QuestionBank> questionBanks = questionBankRepository.findByCreatedByOrderByCreatedAtDesc(currentUserId, pageable);
            log.info("Found {} question banks for user {} (page {}, size {})",
                    questionBanks.getTotalElements(), currentUserId, pageable.getPageNumber(), pageable.getPageSize());
            return questionBanks.map(questionBankMapper::toResponse);
        } catch (Exception e) {
            log.error("Error getting question banks for current user with pagination: {}", e.getMessage(), e);
            throw new BadRequestException("Lỗi khi lấy danh sách câu hỏi: " + e.getMessage());
        }
    }

    @Override
    public QuestionBankResponse getQuestionBankById(Long id) {
        try {
            UUID currentUserId = accountUtils.getCurrentUserId();
            QuestionBank questionBank = questionBankRepository.findByIdAndCreatedBy(id, currentUserId)
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
            if (request.getIsActive() != null) {
                questionBank.setIsActive(request.getIsActive());
            }

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

            // Soft delete by setting isActive to false
            questionBank.setIsActive(false);
            questionBank.setUpdatedBy(currentUserId);
            questionBankRepository.save(questionBank);
            log.info("Soft deleted question bank {} by user {}", id, currentUserId);
        } catch (Exception e) {
            log.error("Error deleting question bank {}: {}", id, e.getMessage(), e);
            throw new BadRequestException("Lỗi khi xóa câu hỏi: " + e.getMessage());
        }
    }

    @Override
    public List<QuestionBankResponse> getQuestionBanksByLessonId(Long lessonId) {
        try {
            UUID currentUserId = accountUtils.getCurrentUserId();
            List<QuestionBank> questionBanks = questionBankRepository.findByCreatedByAndLessonIdOrderByCreatedAtDesc(currentUserId, lessonId);
            log.info("Found {} question banks for lesson {} by user {}", questionBanks.size(), lessonId, currentUserId);
            return questionBanks.stream()
                    .map(questionBankMapper::toResponse)
                    .toList();
        } catch (Exception e) {
            log.error("Error getting question banks by lesson id {}: {}", lessonId, e.getMessage(), e);
            throw new BadRequestException("Lỗi khi lấy câu hỏi theo bài học: " + e.getMessage());
        }
    }

    @Override
    public List<QuestionBankResponse> getQuestionBanksByQuestionType(QuestionType questionType) {
        try {
            UUID currentUserId = accountUtils.getCurrentUserId();
            List<QuestionBank> questionBanks = questionBankRepository.findByCreatedByAndQuestionTypeOrderByCreatedAtDesc(currentUserId, questionType);
            log.info("Found {} question banks for type {} by user {}", questionBanks.size(), questionType, currentUserId);
            return questionBanks.stream()
                    .map(questionBankMapper::toResponse)
                    .toList();
        } catch (Exception e) {
            log.error("Error getting question banks by question type {}: {}", questionType, e.getMessage(), e);
            throw new BadRequestException("Lỗi khi lấy câu hỏi theo loại: " + e.getMessage());
        }
    }

    @Override
    public List<QuestionBankResponse> getQuestionBanksByDifficultyLevel(DifficultyLevel difficultyLevel) {
        try {
            UUID currentUserId = accountUtils.getCurrentUserId();
            List<QuestionBank> questionBanks = questionBankRepository.findByCreatedByAndDifficultyLevelOrderByCreatedAtDesc(currentUserId, difficultyLevel);
            log.info("Found {} question banks for difficulty {} by user {}", questionBanks.size(), difficultyLevel, currentUserId);
            return questionBanks.stream()
                    .map(questionBankMapper::toResponse)
                    .toList();
        } catch (Exception e) {
            log.error("Error getting question banks by difficulty level {}: {}", difficultyLevel, e.getMessage(), e);
            throw new BadRequestException("Lỗi khi lấy câu hỏi theo độ khó: " + e.getMessage());
        }
    }

    @Override
    public List<QuestionBankResponse> getQuestionBanksByFilters(Long lessonId, QuestionType questionType, DifficultyLevel difficultyLevel) {
        try {
            UUID currentUserId = accountUtils.getCurrentUserId();
            List<QuestionBank> questionBanks = questionBankRepository.findByFilters(currentUserId, lessonId, questionType, difficultyLevel);
            log.info("Found {} question banks with filters (lesson: {}, type: {}, difficulty: {}) by user {}",
                    questionBanks.size(), lessonId, questionType, difficultyLevel, currentUserId);
            return questionBanks.stream()
                    .map(questionBankMapper::toResponse)
                    .toList();
        } catch (Exception e) {
            log.error("Error getting question banks by filters: {}", e.getMessage(), e);
            throw new BadRequestException("Lỗi khi lọc câu hỏi: " + e.getMessage());
        }
    }

    @Override
    public Page<QuestionBankResponse> getQuestionBanksByFilters(Long lessonId, QuestionType questionType, DifficultyLevel difficultyLevel, Pageable pageable) {
        try {
            UUID currentUserId = accountUtils.getCurrentUserId();
            Page<QuestionBank> questionBanks = questionBankRepository.findByFilters(currentUserId, lessonId, questionType, difficultyLevel, pageable);
            log.info("Found {} question banks with filters (lesson: {}, type: {}, difficulty: {}) by user {} (page {}, size {})",
                    questionBanks.getTotalElements(), lessonId, questionType, difficultyLevel, currentUserId,
                    pageable.getPageNumber(), pageable.getPageSize());
            return questionBanks.map(questionBankMapper::toResponse);
        } catch (Exception e) {
            log.error("Error getting question banks by filters with pagination: {}", e.getMessage(), e);
            throw new BadRequestException("Lỗi khi lọc câu hỏi: " + e.getMessage());
        }
    }

    @Override
    public List<QuestionBankResponse> searchQuestionBanks(String keyword) {
        try {
            UUID currentUserId = accountUtils.getCurrentUserId();
            List<QuestionBank> questionBanks = questionBankRepository.searchByKeyword(currentUserId, keyword);
            log.info("Found {} question banks for keyword '{}' by user {}", questionBanks.size(), keyword, currentUserId);
            return questionBanks.stream()
                    .map(questionBankMapper::toResponse)
                    .toList();
        } catch (Exception e) {
            log.error("Error searching question banks with keyword '{}': {}", keyword, e.getMessage(), e);
            throw new BadRequestException("Lỗi khi tìm kiếm câu hỏi: " + e.getMessage());
        }
    }

    @Override
    public Page<QuestionBankResponse> searchQuestionBanks(String keyword, Pageable pageable) {
        try {
            UUID currentUserId = accountUtils.getCurrentUserId();
            Page<QuestionBank> questionBanks = questionBankRepository.searchByKeyword(currentUserId, keyword, pageable);
            log.info("Found {} question banks for keyword '{}' by user {} (page {}, size {})",
                    questionBanks.getTotalElements(), keyword, currentUserId,
                    pageable.getPageNumber(), pageable.getPageSize());
            return questionBanks.map(questionBankMapper::toResponse);
        } catch (Exception e) {
            log.error("Error searching question banks with keyword '{}' with pagination: {}", keyword, e.getMessage(), e);
            throw new BadRequestException("Lỗi khi tìm kiếm câu hỏi: " + e.getMessage());
        }
    }





    @Override
    public QuestionBankStatistics getQuestionBankStatistics() {
        try {
            UUID currentUserId = accountUtils.getCurrentUserId();

            // Get all active question banks for current user
            List<QuestionBank> allQuestionBanks = questionBankRepository.findByCreatedByOrderByCreatedAtDesc(currentUserId);

            // Calculate basic statistics
            Long totalQuestions = (long) allQuestionBanks.size();
            Long activeQuestions = allQuestionBanks.stream()
                    .filter(qb -> qb.getIsActive() != null && qb.getIsActive())
                    .count();
            // Group by question type
            Map<QuestionType, Long> questionsByType = allQuestionBanks.stream()
                    .filter(qb -> qb.getIsActive() != null && qb.getIsActive())
                    .collect(Collectors.groupingBy(
                            QuestionBank::getQuestionType,
                            Collectors.counting()
                    ));

            // Group by difficulty level
            Map<DifficultyLevel, Long> questionsByDifficulty = allQuestionBanks.stream()
                    .filter(qb -> qb.getIsActive() != null && qb.getIsActive())
                    .collect(Collectors.groupingBy(
                            QuestionBank::getDifficultyLevel,
                            Collectors.counting()
                    ));

            // Group by lesson
            Map<Long, Long> questionsByLesson = allQuestionBanks.stream()
                    .filter(qb -> qb.getIsActive() != null && qb.getIsActive())
                    .collect(Collectors.groupingBy(
                            QuestionBank::getLessonId,
                            Collectors.counting()
                    ));

            log.info("Generated statistics for user {}: total={}, active={}",
                    currentUserId, totalQuestions, activeQuestions);

            return new QuestionBankStatistics(
                    totalQuestions,
                    activeQuestions,
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
            log.info("Found {} question banks with multiple filters (lesson: {}, types: {}, difficulties: {}) by user {}",
                    questionBanks.size(), lessonId, questionTypes, difficultyLevels, currentUserId);
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
            log.info("Found {} question banks with multiple filters (lesson: {}, types: {}, difficulties: {}) by user {} (page {}, size {})",
                    questionBanks.getTotalElements(), lessonId, questionTypes, difficultyLevels, currentUserId,
                    pageable.getPageNumber(), pageable.getPageSize());
            return questionBanks.map(questionBankMapper::toResponse);
        } catch (Exception e) {
            log.error("Error getting question banks by multiple filters with pagination: {}", e.getMessage(), e);
            throw new BadRequestException("Lỗi khi lọc câu hỏi: " + e.getMessage());
        }
    }


}
