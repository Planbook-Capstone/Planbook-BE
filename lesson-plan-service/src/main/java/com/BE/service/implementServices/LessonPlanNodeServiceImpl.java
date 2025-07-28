package com.BE.service.implementServices;

import com.BE.enums.Status;
import com.BE.mapper.LessonPlanNodeMapper;
import com.BE.model.entity.LessonPlanNode;
import com.BE.model.request.CreateLessonPlanNodeRequest;
import com.BE.model.request.UpdateLessonPlanNodeRequest;
import com.BE.model.response.LessonPlanNodeDTO;
import com.BE.repository.LessonPlanNodeRepository;
import com.BE.service.interfaceServices.LessonPlanNodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of LessonPlanNodeService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LessonPlanNodeServiceImpl implements LessonPlanNodeService {

    private final LessonPlanNodeRepository lessonPlanNodeRepository;
    private final LessonPlanNodeMapper lessonPlanNodeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<LessonPlanNodeDTO> getLessonTree(Long lessonPlanId) {
        log.info("Getting root nodes only for lesson plan ID: {}", lessonPlanId);

        // Get all active root nodes (nodes without parent) for the lesson plan
        List<LessonPlanNode> rootNodes = lessonPlanNodeRepository
                .findByLessonPlanTemplateIdAndParentIsNullAndStatusOrderByOrderIndex(lessonPlanId, Status.ACTIVE);

        if (rootNodes.isEmpty()) {
            log.info("No root nodes found for lesson plan ID: {}", lessonPlanId);
            return List.of();
        }

        // Convert to DTOs - only root nodes without children populated
        List<LessonPlanNodeDTO> result = lessonPlanNodeMapper.toDTOList(rootNodes);

        log.info("Successfully retrieved {} root nodes for lesson plan ID: {}",
                result.size(), lessonPlanId);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonPlanNodeDTO> getNodeChildren(Long nodeId) {
        log.info("Getting all children recursively for node ID: {}", nodeId);

        // Verify parent node exists
        LessonPlanNode parentNode = lessonPlanNodeRepository.findById(nodeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy node với ID: " + nodeId));

        // Get active direct children of the node
        List<LessonPlanNode> children = lessonPlanNodeRepository
                .findByParentIdAndStatusOrderByOrderIndex(nodeId, Status.ACTIVE);

        if (children.isEmpty()) {
            log.info("No children found for node ID: {}", nodeId);
            return List.of();
        }

        // Convert to DTOs with full recursive children
        List<LessonPlanNodeDTO> result = lessonPlanNodeMapper.toDTOList(children);

        log.info("Successfully retrieved {} children with full tree for node ID: {}",
                result.size(), nodeId);

        return result;
    }

    @Override
    public LessonPlanNodeDTO createNode(CreateLessonPlanNodeRequest request) {
        log.info("Creating new lesson plan node: {}", request.getTitle());
        
        // Convert request to entity
        LessonPlanNode newNode = lessonPlanNodeMapper.toEntity(request);
        
        // Set parent if parentId is provided
        if (request.getParentId() != null) {
            LessonPlanNode parent = lessonPlanNodeRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Node cha không tồn tại với ID: " + request.getParentId()));
            
            // Validate that parent belongs to the same lesson plan
            if (!parent.getLessonPlanTemplateId().equals(request.getLessonPlanTemplateId())) {
                throw new RuntimeException("Node cha không thuộc cùng giáo án");
            }
            
            newNode.setParent(parent);
            
            // FE phải truyền orderIndex
            if (request.getOrderIndex() == null) {
                throw new IllegalArgumentException("orderIndex là bắt buộc");
            }
            newNode.setOrderIndex(request.getOrderIndex());
        } else {
            // Root node - FE phải truyền orderIndex
            if (request.getOrderIndex() == null) {
                throw new IllegalArgumentException("orderIndex là bắt buộc");
            }
            newNode.setOrderIndex(request.getOrderIndex());
        }
        
        // Save the new node
        LessonPlanNode savedNode = lessonPlanNodeRepository.save(newNode);
        
        log.info("Successfully created lesson plan node with ID: {}", savedNode.getId());
        
        return lessonPlanNodeMapper.toDTO(savedNode);
    }

    @Override
    public LessonPlanNodeDTO updateNode(Long id, UpdateLessonPlanNodeRequest request) {
        log.info("Updating lesson plan node with ID: {}", id);
        
        // Find existing node
        LessonPlanNode existingNode = lessonPlanNodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy node với ID: " + id));
        
        // Update fields from request
        lessonPlanNodeMapper.updateEntityFromRequest(existingNode, request);
        
        // Save updated node
        LessonPlanNode updatedNode = lessonPlanNodeRepository.save(existingNode);
        
        log.info("Successfully updated lesson plan node with ID: {}", id);
        
        return lessonPlanNodeMapper.toDTO(updatedNode);
    }

    @Override
    @Transactional
    public void deleteNode(Long id) {
        log.info("Soft deleting lesson plan node with ID: {}", id);

        // Check if node exists and is active
        LessonPlanNode nodeToDelete = lessonPlanNodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy node với ID: " + id));

        if (nodeToDelete.getStatus() == Status.INACTIVE) {
            throw new RuntimeException("Node đã bị xóa trước đó");
        }

        // Count children before deletion for logging
        int childrenCount = countAllActiveChildren(nodeToDelete);

        // Soft delete node and all its children recursively
        softDeleteNodeAndChildren(nodeToDelete);

        log.info("Successfully soft deleted lesson plan node with ID: {} and {} children", id, childrenCount);
    }

    /**
     * Helper method to count all active children recursively
     */
    private int countAllActiveChildren(LessonPlanNode node) {
        int count = 0;
        if (node.getChildren() != null) {
            for (LessonPlanNode child : node.getChildren()) {
                if (child.getStatus() == Status.ACTIVE) {
                    count++;
                    count += countAllActiveChildren(child);
                }
            }
        }
        return count;
    }

    /**
     * Helper method to soft delete node and all its children recursively
     */
    private void softDeleteNodeAndChildren(LessonPlanNode node) {
        // Set node status to INACTIVE
        node.setStatus(Status.INACTIVE);
        lessonPlanNodeRepository.save(node);

        // Recursively soft delete all children
        if (node.getChildren() != null) {
            for (LessonPlanNode child : node.getChildren()) {
                if (child.getStatus() == Status.ACTIVE) {
                    softDeleteNodeAndChildren(child);
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonPlanNodeDTO> getAllNodesByLessonPlanTemplateId(Long lessonPlanTemplateId) {
        log.info("Getting all nodes (including inactive) for lesson plan ID: {}", lessonPlanTemplateId);

        // Get all root nodes (both active and inactive) for the lesson plan
        List<LessonPlanNode> allRootNodes = lessonPlanNodeRepository
                .findByLessonPlanTemplateIdAndParentIsNullOrderByOrderIndex(lessonPlanTemplateId);

        if (allRootNodes.isEmpty()) {
            log.info("No nodes found for lesson plan ID: {}", lessonPlanTemplateId);
            return List.of();
        }

        // Convert to DTOs with full tree structure (including inactive children)
        List<LessonPlanNodeDTO> result = lessonPlanNodeMapper.toDTOList(allRootNodes);

        log.info("Successfully retrieved {} root nodes (including inactive) for lesson plan ID: {}",
                result.size(), lessonPlanTemplateId);

        return result;
    }
}
