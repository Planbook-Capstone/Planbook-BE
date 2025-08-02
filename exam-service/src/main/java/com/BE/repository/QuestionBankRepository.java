package com.BE.repository;

import com.BE.enums.DifficultyLevel;
import com.BE.enums.QuestionType;
import com.BE.model.entity.QuestionBank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionBankRepository extends JpaRepository<QuestionBank, Long> {
    
    /**
     * Find all question banks created by a specific user, ordered by creation date descending
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE qb.createdBy = :createdBy AND qb.isActive = true ORDER BY qb.createdAt DESC")
    List<QuestionBank> findByCreatedByOrderByCreatedAtDesc(@Param("createdBy") UUID createdBy);
    
    /**
     * Find question banks with pagination by creator
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE qb.createdBy = :createdBy AND qb.isActive = true ORDER BY qb.createdAt DESC")
    Page<QuestionBank> findByCreatedByOrderByCreatedAtDesc(@Param("createdBy") UUID createdBy, Pageable pageable);
    
    /**
     * Find question banks by creator and lesson ID
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE qb.createdBy = :createdBy AND qb.lessonId = :lessonId AND qb.isActive = true ORDER BY qb.createdAt DESC")
    List<QuestionBank> findByCreatedByAndLessonIdOrderByCreatedAtDesc(@Param("createdBy") UUID createdBy, @Param("lessonId") Long lessonId);

    /**
     * Find question banks by creator and question type
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE qb.createdBy = :createdBy AND qb.questionType = :questionType AND qb.isActive = true ORDER BY qb.createdAt DESC")
    List<QuestionBank> findByCreatedByAndQuestionTypeOrderByCreatedAtDesc(@Param("createdBy") UUID createdBy, @Param("questionType") QuestionType questionType);

    /**
     * Find question banks by creator and difficulty level
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE qb.createdBy = :createdBy AND qb.difficultyLevel = :difficultyLevel AND qb.isActive = true ORDER BY qb.createdAt DESC")
    List<QuestionBank> findByCreatedByAndDifficultyLevelOrderByCreatedAtDesc(@Param("createdBy") UUID createdBy, @Param("difficultyLevel") DifficultyLevel difficultyLevel);
    
    /**
     * Find question banks with multiple filters
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE qb.createdBy = :createdBy " +
           "AND (:lessonId IS NULL OR qb.lessonId = :lessonId) " +
           "AND (:questionType IS NULL OR qb.questionType = :questionType) " +
           "AND (:difficultyLevel IS NULL OR qb.difficultyLevel = :difficultyLevel) " +
           "AND qb.isActive = true " +
           "ORDER BY qb.createdAt DESC")
    List<QuestionBank> findByFilters(@Param("createdBy") UUID createdBy,
                                   @Param("lessonId") Long lessonId,
                                   @Param("questionType") QuestionType questionType,
                                   @Param("difficultyLevel") DifficultyLevel difficultyLevel);

    /**
     * Find question banks with multiple filters and pagination
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE qb.createdBy = :createdBy " +
           "AND (:lessonId IS NULL OR qb.lessonId = :lessonId) " +
           "AND (:questionType IS NULL OR qb.questionType = :questionType) " +
           "AND (:difficultyLevel IS NULL OR qb.difficultyLevel = :difficultyLevel) " +
           "AND qb.isActive = true " +
           "ORDER BY qb.createdAt DESC")
    Page<QuestionBank> findByFilters(@Param("createdBy") UUID createdBy,
                                   @Param("lessonId") Long lessonId,
                                   @Param("questionType") QuestionType questionType,
                                   @Param("difficultyLevel") DifficultyLevel difficultyLevel,
                                   Pageable pageable);
    
    /**
     * Search question banks by keyword in explanation or reference source
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE qb.createdBy = :createdBy " +
           "AND qb.isActive = true " +
           "AND (LOWER(qb.explanation) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(qb.referenceSource) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY qb.createdAt DESC")
    List<QuestionBank> searchByKeyword(@Param("createdBy") UUID createdBy, @Param("keyword") String keyword);

    /**
     * Search question banks by keyword with pagination
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE qb.createdBy = :createdBy " +
           "AND qb.isActive = true " +
           "AND (LOWER(qb.explanation) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(qb.referenceSource) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY qb.createdAt DESC")
    Page<QuestionBank> searchByKeyword(@Param("createdBy") UUID createdBy, @Param("keyword") String keyword, Pageable pageable);
    
    /**
     * Find question bank by ID and creator (for authorization)
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE qb.id = :id AND qb.createdBy = :createdBy AND qb.isActive = true")
    Optional<QuestionBank> findByIdAndCreatedBy(@Param("id") Long id, @Param("createdBy") UUID createdBy);
    
    /**
     * Count question banks by creator
     */
    @Query("SELECT COUNT(qb) FROM QuestionBank qb WHERE qb.createdBy = :createdBy AND qb.isActive = true")
    Long countByCreatedBy(@Param("createdBy") UUID createdBy);
    


    /**
     * Find question banks with multiple filters (supports multiple types and difficulties)
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE qb.createdBy = :createdBy " +
           "AND (:lessonId IS NULL OR qb.lessonId = :lessonId) " +
           "AND (:questionTypes IS NULL OR qb.questionType IN :questionTypes) " +
           "AND (:difficultyLevels IS NULL OR qb.difficultyLevel IN :difficultyLevels) " +
           "AND qb.isActive = true " +
           "ORDER BY qb.createdAt DESC")
    List<QuestionBank> findByMultipleFilters(@Param("createdBy") UUID createdBy,
                                           @Param("lessonId") Long lessonId,
                                           @Param("questionTypes") List<QuestionType> questionTypes,
                                           @Param("difficultyLevels") List<DifficultyLevel> difficultyLevels);

    /**
     * Find question banks with multiple filters and pagination (supports multiple types and difficulties)
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE qb.createdBy = :createdBy " +
           "AND (:lessonId IS NULL OR qb.lessonId = :lessonId) " +
           "AND (:questionTypes IS NULL OR qb.questionType IN :questionTypes) " +
           "AND (:difficultyLevels IS NULL OR qb.difficultyLevel IN :difficultyLevels) " +
           "AND qb.isActive = true " +
           "ORDER BY qb.createdAt DESC")
    Page<QuestionBank> findByMultipleFilters(@Param("createdBy") UUID createdBy,
                                           @Param("lessonId") Long lessonId,
                                           @Param("questionTypes") List<QuestionType> questionTypes,
                                           @Param("difficultyLevels") List<DifficultyLevel> difficultyLevels,
                                           Pageable pageable);
}
