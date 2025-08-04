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
            UUID currentUserId = accountUtils.getCurrentUserId();
            QuestionBank questionBank = questionBankMapper.toEntity(request, currentUserId);

            // Set visibility based on user role
            boolean isStaff = accountUtils.isCurrentUserStaff();
            questionBank.setVisibility(QuestionBankVisibility.getByUserRole(isStaff));

            QuestionBank savedQuestionBank = questionBankRepository.save(questionBank);
            log.info("Đã tạo ngân hàng câu hỏi {} với quyền truy cập {} bởi người dùng {}",
                    savedQuestionBank.getId(), savedQuestionBank.getVisibility(), currentUserId);

            return questionBankMapper.toResponse(savedQuestionBank);

        } catch (Exception e) {
            log.error("Lỗi khi tạo ngân hàng câu hỏi: {}", e.getMessage(), e);
            throw new BadRequestException("Lỗi khi tạo câu hỏi: " + e.getMessage());
        }
    }

    @Override
    public QuestionBankResponse getQuestionBankById(Long id) {
        try {
            UUID currentUserId = accountUtils.getCurrentUserId();
            QuestionBank questionBank = questionBankRepository.findByIdAndAccessible(id, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ngân hàng câu hỏi với id: " + id + " hoặc bạn không có quyền truy cập"));
            log.info("Đã lấy thông tin ngân hàng câu hỏi {} cho người dùng {}", id, currentUserId);
            return questionBankMapper.toResponse(questionBank);
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin ngân hàng câu hỏi với id {}: {}", id, e.getMessage(), e);
            throw new BadRequestException("Lỗi khi lấy thông tin câu hỏi: " + e.getMessage());
        }
    }

    @Override
    public QuestionBankResponse updateQuestionBank(Long id, UpdateQuestionBankRequest request) {
        try {
            UUID currentUserId = accountUtils.getCurrentUserId();
            QuestionBank questionBank = questionBankRepository.findByIdAndCreatedBy(id, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ngân hàng câu hỏi với id: " + id + " hoặc bạn không có quyền truy cập"));

            // Update fields if provided
            if (request.getLessonIds() != null) {
                questionBank.setLessonIds(request.getLessonIds());
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
            // Lưu ý: Quyền truy cập không thể được cập nhật sau khi tạo để duy trì tính toàn vẹn dữ liệu

            questionBank.setUpdatedBy(currentUserId);
            QuestionBank savedQuestionBank = questionBankRepository.save(questionBank);
            log.info("Đã cập nhật ngân hàng câu hỏi {} bởi người dùng {}", id, currentUserId);
            return questionBankMapper.toResponse(savedQuestionBank);
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật ngân hàng câu hỏi {}: {}", id, e.getMessage(), e);
            throw new BadRequestException("Lỗi khi cập nhật câu hỏi: " + e.getMessage());
        }
    }

    @Override
    public void deleteQuestionBank(Long id) {
        try {
            UUID currentUserId = accountUtils.getCurrentUserId();
            QuestionBank questionBank = questionBankRepository.findByIdAndCreatedBy(id, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ngân hàng câu hỏi với id: " + id + " hoặc bạn không có quyền truy cập"));

            // Xóa vĩnh viễn ngân hàng câu hỏi
            questionBankRepository.delete(questionBank);
            log.info("Đã xóa ngân hàng câu hỏi {} bởi người dùng {}", id, currentUserId);
        } catch (Exception e) {
            log.error("Lỗi khi xóa ngân hàng câu hỏi {}: {}", id, e.getMessage(), e);
            throw new BadRequestException("Lỗi khi xóa câu hỏi: " + e.getMessage());
        }
    }











    @Override
    public List<QuestionBankResponse> getQuestionBanksByFilters(List<Long> lessonIds, List<QuestionType> questionTypes, List<DifficultyLevel> difficultyLevels) {
        try {
            UUID currentUserId = accountUtils.getCurrentUserId();

            // Convert enums to strings for native query
            List<String> questionTypeStrings = questionTypes != null ?
                questionTypes.stream().map(Enum::name).toList() : null;
            List<String> difficultyLevelStrings = difficultyLevels != null ?
                difficultyLevels.stream().map(Enum::name).toList() : null;

            // Convert lesson IDs to JSON string for MySQL JSON_OVERLAPS function
            String lessonIdsJson = null;
            if (lessonIds != null && !lessonIds.isEmpty()) {
                try {
                    lessonIdsJson = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(lessonIds);
                } catch (Exception e) {
                    log.error("Error converting lesson IDs to JSON: {}", e.getMessage());
                    throw new BadRequestException("Invalid lesson IDs format");
                }
            }

            List<QuestionBank> questionBanks = questionBankRepository.findByMultipleFilters(
                currentUserId, lessonIdsJson, questionTypeStrings, difficultyLevelStrings);

            if ((lessonIds == null || lessonIds.isEmpty()) && (questionTypes == null || questionTypes.isEmpty()) && (difficultyLevels == null || difficultyLevels.isEmpty())) {
                log.info("Tìm thấy {} ngân hàng câu hỏi (tất cả có thể truy cập) cho người dùng {}", questionBanks.size(), currentUserId);
            } else {
                log.info("Tìm thấy {} ngân hàng câu hỏi với bộ lọc (bài học: {}, loại: {}, độ khó: {}) cho người dùng {}",
                        questionBanks.size(), lessonIds, questionTypes, difficultyLevels, currentUserId);
            }
            return questionBanks.stream()
                    .map(questionBankMapper::toResponse)
                    .toList();
        } catch (Exception e) {
            log.error("Lỗi khi lấy ngân hàng câu hỏi theo nhiều bộ lọc: {}", e.getMessage(), e);
            throw new BadRequestException("Lỗi khi lọc câu hỏi: " + e.getMessage());
        }
    }

    @Override
    public Page<QuestionBankResponse> getQuestionBanksByFilters(List<Long> lessonIds, List<QuestionType> questionTypes, List<DifficultyLevel> difficultyLevels, Pageable pageable) {
        try {
            UUID currentUserId = accountUtils.getCurrentUserId();

            // Convert enums to strings for native query
            List<String> questionTypeStrings = questionTypes != null ?
                questionTypes.stream().map(Enum::name).toList() : null;
            List<String> difficultyLevelStrings = difficultyLevels != null ?
                difficultyLevels.stream().map(Enum::name).toList() : null;

            // Convert lesson IDs to JSON string for MySQL JSON_OVERLAPS function
            String lessonIdsJson = null;
            if (lessonIds != null && !lessonIds.isEmpty()) {
                try {
                    lessonIdsJson = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(lessonIds);
                } catch (Exception e) {
                    log.error("Error converting lesson IDs to JSON: {}", e.getMessage());
                    throw new BadRequestException("Invalid lesson IDs format");
                }
            }

            Page<QuestionBank> questionBanks = questionBankRepository.findByMultipleFilters(
                currentUserId, lessonIdsJson, questionTypeStrings, difficultyLevelStrings, pageable);

            if ((lessonIds == null || lessonIds.isEmpty()) && (questionTypes == null || questionTypes.isEmpty()) && (difficultyLevels == null || difficultyLevels.isEmpty())) {
                log.info("Tìm thấy {} ngân hàng câu hỏi (tất cả có thể truy cập) cho người dùng {} (trang {}, kích thước {})",
                        questionBanks.getTotalElements(), currentUserId, pageable.getPageNumber(), pageable.getPageSize());
            } else {
                log.info("Tìm thấy {} ngân hàng câu hỏi với bộ lọc (bài học: {}, loại: {}, độ khó: {}) cho người dùng {} (trang {}, kích thước {})",
                        questionBanks.getTotalElements(), lessonIds, questionTypes, difficultyLevels, currentUserId,
                        pageable.getPageNumber(), pageable.getPageSize());
            }
            return questionBanks.map(questionBankMapper::toResponse);
        } catch (Exception e) {
            log.error("Lỗi khi lấy ngân hàng câu hỏi theo nhiều bộ lọc với phân trang: {}", e.getMessage(), e);
            throw new BadRequestException("Lỗi khi lọc câu hỏi: " + e.getMessage());
        }
    }
}
