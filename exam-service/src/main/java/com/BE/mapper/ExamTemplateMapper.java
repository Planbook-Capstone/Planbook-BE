package com.BE.mapper;

import com.BE.model.entity.ExamTemplate;
import com.BE.model.request.CreateExamTemplateRequest;
import com.BE.model.request.UpdateExamTemplateRequest;
import com.BE.model.response.ExamTemplateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.UUID;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ExamTemplateMapper {

    /**
     * Map CreateExamTemplateRequest to ExamTemplate entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", source = "teacherId")
    @Mapping(target = "version", constant = "1")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "examInstances", ignore = true)
    ExamTemplate toEntity(CreateExamTemplateRequest request, UUID teacherId);

    /**
     * Map ExamTemplate entity to ExamTemplateResponse
     */
    ExamTemplateResponse toResponse(ExamTemplate entity);

    /**
     * Update ExamTemplate entity from UpdateExamTemplateRequest
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "examInstances", ignore = true)
    void updateEntity(@MappingTarget ExamTemplate entity, UpdateExamTemplateRequest request);

    /**
     * Clone ExamTemplate entity for duplication
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", source = "teacherId")
    @Mapping(target = "version", constant = "1")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "examInstances", ignore = true)
    @Mapping(target = "name", expression = "java(original.getName() + \" (Copy)\")")
    ExamTemplate cloneEntity(ExamTemplate original, UUID teacherId);
}
