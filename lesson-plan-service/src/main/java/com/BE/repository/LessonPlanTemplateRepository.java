package com.BE.repository;

import com.BE.enums.Status;
import com.BE.model.entity.LessonPlanTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for LessonPlan entity
 */
@Repository
public interface LessonPlanTemplateRepository extends JpaRepository<LessonPlanTemplate, Long> {

    /**
     * Find active lesson plan by ID
     */
    Optional<LessonPlanTemplate> findByIdAndStatus(Long id, Status status);

    /**
     * Find lesson plans with search functionality and optional status filter
     */
    @Query("SELECT lp FROM LessonPlanTemplate lp WHERE " +
           "(:status IS NULL OR lp.status = :status) AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(lp.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(lp.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY lp.createdAt DESC")
    Page<LessonPlanTemplate> findWithSearchAndStatus(@Param("keyword") String keyword,
                                                     @Param("status") Status status,
                                                     Pageable pageable);

//    /**
//     * Count active lesson plans
//     */
//    long countByStatus(Status status);
}
