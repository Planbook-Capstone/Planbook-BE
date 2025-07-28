package com.BE.mapper;

import com.BE.model.response.LessonPlanTemplateDTO;
import com.BE.model.entity.LessonPlanTemplate;
import com.BE.model.request.CreateLessonPlanRequest;
import com.BE.model.request.UpdateLessonPlanRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * Mapper for converting between LessonPlan entity and DTOs
 */
@Mapper(componentModel = "spring")
public interface LessonPlanTemplateMapper {
    // Entity → DTO
    LessonPlanTemplateDTO toDTO(LessonPlanTemplate entity);

    // List<Entity> → List<DTO>
    List<LessonPlanTemplateDTO> toDTOList(List<LessonPlanTemplate> entities);

    // CreateRequest → Entity
    LessonPlanTemplate toEntity(CreateLessonPlanRequest request);

    // Update entity with values from request
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(@MappingTarget LessonPlanTemplate entity, UpdateLessonPlanRequest request);
}
