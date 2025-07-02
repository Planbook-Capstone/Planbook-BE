package com.BE.mapper;

import com.BE.model.dto.LessonPlanDTO;
import com.BE.model.entity.LessonPlan;
import com.BE.model.request.CreateLessonPlanRequest;
import com.BE.model.request.UpdateLessonPlanRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between LessonPlan entity and DTOs
 */
@Component
public class LessonPlanMapper {

    /**
     * Convert entity to DTO
     */
    public LessonPlanDTO toDTO(LessonPlan entity) {
        if (entity == null) {
            return null;
        }

        LessonPlanDTO dto = new LessonPlanDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setStatus(entity.getStatus());
        
        return dto;
    }

    /**
     * Convert list of entities to DTOs
     */
    public List<LessonPlanDTO> toDTOList(List<LessonPlan> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert create request to entity
     */
    public LessonPlan toEntity(CreateLessonPlanRequest request) {
        if (request == null) {
            return null;
        }

        return LessonPlan.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    /**
     * Update entity from update request
     */
    public void updateEntityFromRequest(LessonPlan entity, UpdateLessonPlanRequest request) {
        if (entity == null || request == null) {
            return;
        }

        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
    }
}
