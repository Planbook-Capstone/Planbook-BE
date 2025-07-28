package com.BE.repository;

import com.BE.enums.Status;
import com.BE.model.entity.LessonPlanNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for LessonPlanNode entity
 */
@Repository
public interface LessonPlanNodeRepository extends JpaRepository<LessonPlanNode, Long> {


    /**
     * Find all active root nodes (nodes without parent) for a specific lesson plan
     */
    List<LessonPlanNode> findByLessonPlanTemplateIdAndParentIsNullAndStatusOrderByOrderIndex(Long lessonPlanTemplateId, Status status);

    /**
     * Find all active children of a specific parent node
     */
    List<LessonPlanNode> findByParentIdAndStatusOrderByOrderIndex(Long parentId, Status status);

    /**
     * Find all root nodes (nodes without parent) for a specific lesson plan
     */
    List<LessonPlanNode> findByLessonPlanTemplateIdAndParentIsNullOrderByOrderIndex(Long lessonPlanTemplateId);


//    // Legacy methods for backward compatibility (will be deprecated)
//    /**
//     * Find all nodes belonging to a specific lesson plan
//     */
//    List<LessonPlanNode> findByLessonPlanIdOrderByOrderIndex(Long lessonPlanId);
//
//
//
//    /**
//     * Find all children of a specific parent node
//     */
//    List<LessonPlanNode> findByParentIdOrderByOrderIndex(Long parentId);
//
//    /**
//     * Find all nodes by parent
//     */
//    List<LessonPlanNode> findByParentOrderByOrderIndex(LessonPlanNode parent);
//
//    /**
//     * Check if a node exists by lesson plan ID and parent ID
//     */
//    boolean existsByLessonPlanIdAndParentId(Long lessonPlanId, Long parentId);
//
//    /**
//     * Count nodes by lesson plan ID
//     */
//    long countByLessonPlanId(Long lessonPlanId);
//
//    /**
//     * Find nodes by lesson plan ID and type
//     */
//    List<LessonPlanNode> findByLessonPlanIdAndTypeOrderByOrderIndex(Long lessonPlanId, com.BE.enums.NodeType type);
//
//
//
//    /**
//     * Get the maximum order index for nodes with the same parent
//     */
//    @Query("SELECT COALESCE(MAX(n.orderIndex), 0) FROM LessonPlanNode n WHERE n.parent = :parent")
//    Integer findMaxOrderIndexByParent(@Param("parent") LessonPlanNode parent);
//
//    /**
//     * Get the maximum order index for root nodes in a lesson plan
//     */
//    @Query("SELECT COALESCE(MAX(n.orderIndex), 0) FROM LessonPlanNode n WHERE n.lessonPlanTemplateId = :lessonPlanTemplateId AND n.parent IS NULL")
//    Integer findMaxOrderIndexByLessonPlanTemplateIdAndParentIsNull(@Param("lessonPlanTemplateId") Long lessonPlanTemplateId);
//
//    /**
//     * Delete all nodes by lesson plan ID
//     */
//    void deleteByLessonPlanId(Long lessonPlanId);
//
//
//    /**
//     * Find all active nodes belonging to a specific lesson plan
//     */
//    List<LessonPlanNode> findByLessonPlanIdAndStatusOrderByOrderIndex(Long lessonPlanId, Status status);
}
