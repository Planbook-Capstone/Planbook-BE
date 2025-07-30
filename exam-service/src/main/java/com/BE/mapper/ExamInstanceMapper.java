package com.BE.mapper;

import com.BE.model.entity.ExamInstance;
import com.BE.model.entity.ExamTemplate;
import com.BE.model.request.CreateExamInstanceRequest;
import com.BE.model.request.UpdateExamInstanceRequest;
import com.BE.model.response.ExamContentResponse;
import com.BE.model.response.ExamInstanceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Map;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ExamInstanceMapper {

    /**
     * Map CreateExamInstanceRequest to ExamInstance entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "template", source = "template")
    @Mapping(target = "code", ignore = true) // Will be set by service
    @Mapping(target = "excelUrl", ignore = true)
    @Mapping(target = "status", ignore = true) // Default value set in entity
    @Mapping(target = "statusChangedAt", ignore = true)
    @Mapping(target = "statusChangeReason", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "submissions", ignore = true)
    ExamInstance toEntity(CreateExamInstanceRequest request, ExamTemplate template);

    /**
     * Map ExamInstance entity to ExamInstanceResponse
     */
    @Mapping(target = "templateId", source = "template.id")
    @Mapping(target = "templateName", source = "template.name")
    @Mapping(target = "durationMinutes", source = "template.durationMinutes")
    @Mapping(target = "subject", source = "template.subject")
    @Mapping(target = "grade", source = "template.grade")
    ExamInstanceResponse toResponse(ExamInstance entity);

    /**
     * Update ExamInstance entity from UpdateExamInstanceRequest
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "template", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "excelUrl", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "statusChangedAt", ignore = true)
    @Mapping(target = "statusChangeReason", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "submissions", ignore = true)
    void updateEntity(@MappingTarget ExamInstance entity, UpdateExamInstanceRequest request);

    /**
     * Map ExamInstance to ExamContentResponse for students
     */
    @Mapping(target = "examInstanceId", source = "entity.id")
    @Mapping(target = "examName", source = "entity.template.name")
    @Mapping(target = "subject", source = "entity.template.subject")
    @Mapping(target = "grade", source = "entity.template.grade")
    @Mapping(target = "durationMinutes", source = "entity.template.durationMinutes")
    @Mapping(target = "school", source = "entity.template.school")
    @Mapping(target = "examCode", source = "entity.template.examCode")
    @Mapping(target = "atomicMasses", source = "entity.template.atomicMasses")
    @Mapping(target = "totalScore", source = "entity.template.totalScore")
    @Mapping(target = "contentJson", source = "studentContent")
    @Mapping(target = "startAt", source = "entity.startAt")
    @Mapping(target = "endAt", source = "entity.endAt")
    @Mapping(target = "code", source = "entity.code")
    ExamContentResponse toContentResponse(ExamInstance entity, Map<String, Object> studentContent);
}
