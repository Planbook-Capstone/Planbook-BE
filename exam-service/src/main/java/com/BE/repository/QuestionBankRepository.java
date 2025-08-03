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
     * Find question banks by creator and lesson ID (for personal use)
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE qb.createdBy = :createdBy AND qb.lessonId = :lessonId ORDER BY qb.createdAt DESC")
    List<QuestionBank> findByCreatedByAndLessonIdOrderByCreatedAtDesc(@Param("createdBy") UUID createdBy, @Param("lessonId") Long lessonId);

    /**
     * Find question banks by lesson ID that user can access (public + own private)
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE " +
           "(qb.visibility = 'PUBLIC' OR qb.createdBy = :currentUserId) " +
           "AND qb.lessonId = :lessonId " +
           "ORDER BY qb.createdAt DESC")
    List<QuestionBank> findByLessonIdAndAccessible(@Param("currentUserId") UUID currentUserId, @Param("lessonId") Long lessonId);

    /**
     * Find question banks by creator and question type (for personal use)
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE qb.createdBy = :createdBy AND qb.questionType = :questionType ORDER BY qb.createdAt DESC")
    List<QuestionBank> findByCreatedByAndQuestionTypeOrderByCreatedAtDesc(@Param("createdBy") UUID createdBy, @Param("questionType") QuestionType questionType);

    /**
     * Find question banks by question type that user can access (public + own private)
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE " +
           "(qb.visibility = 'PUBLIC' OR qb.createdBy = :currentUserId) " +
           "AND qb.questionType = :questionType " +
           "ORDER BY qb.createdAt DESC")
    List<QuestionBank> findByQuestionTypeAndAccessible(@Param("currentUserId") UUID currentUserId, @Param("questionType") QuestionType questionType);

    /**
     * Find question banks by creator and difficulty level (for personal use)
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE qb.createdBy = :createdBy AND qb.difficultyLevel = :difficultyLevel ORDER BY qb.createdAt DESC")
    List<QuestionBank> findByCreatedByAndDifficultyLevelOrderByCreatedAtDesc(@Param("createdBy") UUID createdBy, @Param("difficultyLevel") DifficultyLevel difficultyLevel);

    /**
     * Find question banks by difficulty level that user can access (public + own private)
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE " +
           "(qb.visibility = 'PUBLIC' OR qb.createdBy = :currentUserId) " +
           "AND qb.difficultyLevel = :difficultyLevel " +
           "ORDER BY qb.createdAt DESC")
    List<QuestionBank> findByDifficultyLevelAndAccessible(@Param("currentUserId") UUID currentUserId, @Param("difficultyLevel") DifficultyLevel difficultyLevel);
    
    /**
     * Find question banks with multiple filters - includes both public questions and user's private questions
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE " +
           "(qb.visibility = 'PUBLIC' OR qb.createdBy = :currentUserId) " +
           "AND (:lessonId IS NULL OR qb.lessonId = :lessonId) " +
           "AND (:questionType IS NULL OR qb.questionType = :questionType) " +
           "AND (:difficultyLevel IS NULL OR qb.difficultyLevel = :difficultyLevel) " +
           "ORDER BY qb.createdAt DESC")
    List<QuestionBank> findByFilters(@Param("currentUserId") UUID currentUserId,
                                   @Param("lessonId") Long lessonId,
                                   @Param("questionType") QuestionType questionType,
                                   @Param("difficultyLevel") DifficultyLevel difficultyLevel);

    /**
     * Find question banks with multiple filters and pagination - includes both public questions and user's private questions
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE " +
           "(qb.visibility = 'PUBLIC' OR qb.createdBy = :currentUserId) " +
           "AND (:lessonId IS NULL OR qb.lessonId = :lessonId) " +
           "AND (:questionType IS NULL OR qb.questionType = :questionType) " +
           "AND (:difficultyLevel IS NULL OR qb.difficultyLevel = :difficultyLevel) " +
           "ORDER BY qb.createdAt DESC")
    Page<QuestionBank> findByFilters(@Param("currentUserId") UUID currentUserId,
                                   @Param("lessonId") Long lessonId,
                                   @Param("questionType") QuestionType questionType,
                                   @Param("difficultyLevel") DifficultyLevel difficultyLevel,
                                   Pageable pageable);
    

    
    /**
     * Find question bank by ID and creator (for authorization)
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE qb.id = :id AND qb.createdBy = :createdBy")
    Optional<QuestionBank> findByIdAndCreatedBy(@Param("id") Long id, @Param("createdBy") UUID createdBy);

    /**
     * Find question bank by ID that user can access (public or owned by user)
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE qb.id = :id AND (qb.visibility = 'PUBLIC' OR qb.createdBy = :currentUserId)")
    Optional<QuestionBank> findByIdAndAccessible(@Param("id") Long id, @Param("currentUserId") UUID currentUserId);

    /**
     * Count question banks by creator
     */
    @Query("SELECT COUNT(qb) FROM QuestionBank qb WHERE qb.createdBy = :createdBy")
    Long countByCreatedBy(@Param("createdBy") UUID createdBy);
    


    /**
     * Find question banks with multiple filters (supports multiple types and difficulties) - includes both public questions and user's private questions
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE " +
           "(qb.visibility = 'PUBLIC' OR qb.createdBy = :currentUserId) " +
           "AND (:lessonId IS NULL OR qb.lessonId = :lessonId) " +
           "AND (:questionTypes IS NULL OR qb.questionType IN :questionTypes) " +
           "AND (:difficultyLevels IS NULL OR qb.difficultyLevel IN :difficultyLevels) " +
           "ORDER BY qb.createdAt DESC")
    List<QuestionBank> findByMultipleFilters(@Param("currentUserId") UUID currentUserId,
                                           @Param("lessonId") Long lessonId,
                                           @Param("questionTypes") List<QuestionType> questionTypes,
                                           @Param("difficultyLevels") List<DifficultyLevel> difficultyLevels);

    /**
     * Find question banks with multiple filters and pagination (supports multiple types and difficulties) - includes both public questions and user's private questions
     */
    @Query("SELECT qb FROM QuestionBank qb WHERE " +
           "(qb.visibility = 'PUBLIC' OR qb.createdBy = :currentUserId) " +
           "AND (:lessonId IS NULL OR qb.lessonId = :lessonId) " +
           "AND (:questionTypes IS NULL OR qb.questionType IN :questionTypes) " +
           "AND (:difficultyLevels IS NULL OR qb.difficultyLevel IN :difficultyLevels) " +
           "ORDER BY qb.createdAt DESC")
    Page<QuestionBank> findByMultipleFilters(@Param("currentUserId") UUID currentUserId,
                                           @Param("lessonId") Long lessonId,
                                           @Param("questionTypes") List<QuestionType> questionTypes,
                                           @Param("difficultyLevels") List<DifficultyLevel> difficultyLevels,
                                           Pageable pageable);
}
