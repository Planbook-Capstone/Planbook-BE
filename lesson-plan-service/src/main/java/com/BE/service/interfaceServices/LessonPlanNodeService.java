package com.BE.service.interfaceServices;

import com.BE.model.request.CreateLessonPlanNodeRequest;
import com.BE.model.request.UpdateLessonPlanNodeRequest;
import com.BE.model.response.LessonPlanNodeDTO;

import java.util.List;

/**
 * Service interface for managing lesson plan nodes
 */
public interface LessonPlanNodeService {

    /**
     * Get only root nodes for a lesson plan (without children populated)
     * @param lessonPlanTemplateId ID of the lesson plan
     * @return List of root nodes only, sorted by orderIndex
     */
    List<LessonPlanNodeDTO> getLessonTree(Long lessonPlanTemplateId);

    /**
     * Get all children of a specific node with full recursive tree structure
     * @param nodeId ID of the parent node
     * @return List of children nodes with their children populated recursively
     */
    List<LessonPlanNodeDTO> getNodeChildren(Long nodeId);

    /**
     * Create a new lesson plan node
     * @param request Create request containing node details
     * @return Created node DTO
     */
    LessonPlanNodeDTO createNode(CreateLessonPlanNodeRequest request);

    /**
     * Update an existing lesson plan node
     * @param id ID of the node to update
     * @param request Update request containing new values
     * @return Updated node DTO
     */
    LessonPlanNodeDTO updateNode(Long id, UpdateLessonPlanNodeRequest request);

    /**
     * Delete a lesson plan node and all its children
     * @param id ID of the node to delete
     */
    void deleteNode(Long id);

    /**
     * Get all nodes (including inactive) for a lesson plan - Admin only
     * Returns all nodes with full tree structure regardless of status
     * @param lessonPlanTemplateId ID of the lesson plan
     * @return List of all nodes (active and inactive) with full tree structure
     */
    List<LessonPlanNodeDTO> getAllNodesByLessonPlanTemplateId(Long lessonPlanTemplateId);
}
