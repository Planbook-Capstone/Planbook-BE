package com.BE.mapper;

import com.BE.model.entity.LessonPlanNode;
import com.BE.model.request.CreateLessonPlanNodeRequest;
import com.BE.model.request.UpdateLessonPlanNodeRequest;
import com.BE.model.response.LessonPlanNodeDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between LessonPlanNode entity and DTOs
 */
@Component
public class LessonPlanNodeMapper {

    /**
     * Convert entity to DTO (without children to avoid circular reference)
     */
    public LessonPlanNodeDTO toDTO(LessonPlanNode entity) {
        if (entity == null) {
            return null;
        }

        return LessonPlanNodeDTO.builder()
                .id(entity.getId())
                .lessonPlanId(entity.getLessonPlanId())
                .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
                .title(entity.getTitle())
                .content(entity.getContent())
                .type(entity.getType())
                .orderIndex(entity.getOrderIndex())
                .metadata(entity.getMetadata())
                .status(entity.getStatus())
                .children(new ArrayList<>()) // Initialize empty list, will be populated separately
                .build();
    }

    /**
     * Convert entity to DTO with children (for tree structure)
     */
    public LessonPlanNodeDTO toDTOWithChildren(LessonPlanNode entity) {
        if (entity == null) {
            return null;
        }

        LessonPlanNodeDTO dto = toDTO(entity);
        
        // Convert children recursively
        if (entity.getChildren() != null && !entity.getChildren().isEmpty()) {
            List<LessonPlanNodeDTO> childrenDTOs = entity.getChildren()
                    .stream()
                    .sorted((a, b) -> a.getOrderIndex().compareTo(b.getOrderIndex()))
                    .map(this::toDTOWithChildren)
                    .collect(Collectors.toList());
            dto.setChildren(childrenDTOs);
        }

        return dto;
    }

    /**
     * Convert list of entities to DTOs with tree structure
     */
    public List<LessonPlanNodeDTO> toDTOList(List<LessonPlanNode> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }

        return entities.stream()
                .map(this::toDTOWithChildren)
                .collect(Collectors.toList());
    }

    /**
     * Convert list of entities to DTOs without children (flat list)
     */
    public List<LessonPlanNodeDTO> toDTOListWithoutChildren(List<LessonPlanNode> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }

        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert create request to entity
     */
    public LessonPlanNode toEntity(CreateLessonPlanNodeRequest request) {
        if (request == null) {
            return null;
        }

        return LessonPlanNode.builder()
                .lessonPlanId(request.getLessonPlanId())
                .title(request.getTitle())
                .content(request.getContent())
                .type(request.getType())
                .orderIndex(request.getOrderIndex())
                .metadata(request.getMetadata())
                .children(new ArrayList<>())
                .build();
    }

    /**
     * Update entity from update request
     */
    public void updateEntityFromRequest(LessonPlanNode entity, UpdateLessonPlanNodeRequest request) {
        if (entity == null || request == null) {
            return;
        }

        if (request.getTitle() != null) {
            entity.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            entity.setContent(request.getContent());
        }
        if (request.getType() != null) {
            entity.setType(request.getType());
        }
        if (request.getOrderIndex() != null) {
            entity.setOrderIndex(request.getOrderIndex());
        }
        if (request.getMetadata() != null) {
            entity.setMetadata(request.getMetadata());
        }
    }
}
