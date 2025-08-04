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
    @Query(value = "SELECT * FROM question_banks qb WHERE qb.created_by = :createdBy AND JSON_CONTAINS(qb.lesson_ids, CAST(:lessonId AS JSON)) ORDER BY qb.created_at DESC", nativeQuery = true)
    List<QuestionBank> findByCreatedByAndLessonIdOrderByCreatedAtDesc(@Param("createdBy") UUID createdBy, @Param("lessonId") Long lessonId);

    /**
     * Find question banks by lesson ID that user can access (public + own private)
     */
    @Query(value = "SELECT * FROM question_banks qb WHERE " +
           "(qb.visibility = 'PUBLIC' OR qb.created_by = :currentUserId) " +
           "AND JSON_CONTAINS(qb.lesson_ids, CAST(:lessonId AS JSON)) " +
           "ORDER BY qb.created_at DESC", nativeQuery = true)
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
    @Query(value = "SELECT * FROM question_banks qb WHERE " +
           "(qb.visibility = 'PUBLIC' OR qb.created_by = :currentUserId) " +
           "AND (:lessonId IS NULL OR JSON_CONTAINS(qb.lesson_ids, CAST(:lessonId AS JSON))) " +
           "AND (:questionType IS NULL OR qb.question_type = :questionType) " +
           "AND (:difficultyLevel IS NULL OR qb.difficulty_level = :difficultyLevel) " +
           "ORDER BY qb.created_at DESC", nativeQuery = true)
    List<QuestionBank> findByFilters(@Param("currentUserId") UUID currentUserId,
                                   @Param("lessonId") Long lessonId,
                                   @Param("questionType") String questionType,
                                   @Param("difficultyLevel") String difficultyLevel);

    /**
     * Find question banks with multiple filters and pagination - includes both public questions and user's private questions
     */
    @Query(value = "SELECT * FROM question_banks qb WHERE " +
           "(qb.visibility = 'PUBLIC' OR qb.created_by = :currentUserId) " +
           "AND (:lessonId IS NULL OR JSON_CONTAINS(qb.lesson_ids, CAST(:lessonId AS JSON))) " +
           "AND (:questionType IS NULL OR qb.question_type = :questionType) " +
           "AND (:difficultyLevel IS NULL OR qb.difficulty_level = :difficultyLevel) " +
           "ORDER BY qb.created_at DESC",
           countQuery = "SELECT COUNT(*) FROM question_banks qb WHERE " +
           "(qb.visibility = 'PUBLIC' OR qb.created_by = :currentUserId) " +
           "AND (:lessonId IS NULL OR JSON_CONTAINS(qb.lesson_ids, CAST(:lessonId AS JSON))) " +
           "AND (:questionType IS NULL OR qb.question_type = :questionType) " +
           "AND (:difficultyLevel IS NULL OR qb.difficulty_level = :difficultyLevel)",
           nativeQuery = true)
    Page<QuestionBank> findByFilters(@Param("currentUserId") UUID currentUserId,
                                   @Param("lessonId") Long lessonId,
                                   @Param("questionType") String questionType,
                                   @Param("difficultyLevel") String difficultyLevel,
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
     * Find question banks with multiple filters (supports multiple types and difficulties) - includes both public questions and user's private questions
     */
    @Query(value = "SELECT * FROM question_banks qb WHERE " +
           "(qb.visibility = 'PUBLIC' OR qb.created_by = :currentUserId) " +
           "AND (:lessonIds IS NULL OR " +
           "JSON_OVERLAPS(qb.lesson_ids, CAST(:lessonIds AS JSON))) " +
           "AND (:questionTypes IS NULL OR qb.question_type IN (:questionTypes)) " +
           "AND (:difficultyLevels IS NULL OR qb.difficulty_level IN (:difficultyLevels)) " +
           "ORDER BY qb.created_at DESC", nativeQuery = true)
    List<QuestionBank> findByMultipleFilters(@Param("currentUserId") UUID currentUserId,
                                           @Param("lessonIds") String lessonIds,
                                           @Param("questionTypes") List<String> questionTypes,
                                           @Param("difficultyLevels") List<String> difficultyLevels);

    /**
     * Find question banks with multiple filters and pagination (supports multiple types and difficulties) - includes both public questions and user's private questions
     */
    @Query(value = "SELECT * FROM question_banks qb WHERE " +
           "(qb.visibility = 'PUBLIC' OR qb.created_by = :currentUserId) " +
           "AND (:lessonIds IS NULL OR " +
           "JSON_OVERLAPS(qb.lesson_ids, CAST(:lessonIds AS JSON))) " +
           "AND (:questionTypes IS NULL OR qb.question_type IN (:questionTypes)) " +
           "AND (:difficultyLevels IS NULL OR qb.difficulty_level IN (:difficultyLevels)) " +
           "ORDER BY qb.created_at DESC",
           countQuery = "SELECT COUNT(*) FROM question_banks qb WHERE " +
           "(qb.visibility = 'PUBLIC' OR qb.created_by = :currentUserId) " +
           "AND (:lessonIds IS NULL OR " +
           "JSON_OVERLAPS(qb.lesson_ids, CAST(:lessonIds AS JSON))) " +
           "AND (:questionTypes IS NULL OR qb.question_type IN (:questionTypes)) " +
           "AND (:difficultyLevels IS NULL OR qb.difficulty_level IN (:difficultyLevels))",
           nativeQuery = true)
    Page<QuestionBank> findByMultipleFilters(@Param("currentUserId") UUID currentUserId,
                                           @Param("lessonIds") String lessonIds,
                                           @Param("questionTypes") List<String> questionTypes,
                                           @Param("difficultyLevels") List<String> difficultyLevels,
                                           Pageable pageable);


}
