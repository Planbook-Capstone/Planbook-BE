package com.BE.mapper;

import com.BE.model.entity.QuestionBank;
import com.BE.model.request.CreateQuestionBankRequest;
import com.BE.model.request.UpdateQuestionBankRequest;
import com.BE.model.response.QuestionBankResponse;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface QuestionBankMapper {

    /**
     * Map CreateQuestionBankRequest to QuestionBank entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    QuestionBank toEntity(CreateQuestionBankRequest request, UUID createdBy);

    /**
     * Map QuestionBank entity to QuestionBankResponse
     */
    @Mapping(target = "questionTypeDescription", source = "questionType.description")
    @Mapping(target = "difficultyLevelDescription", source = "difficultyLevel.description")
    QuestionBankResponse toResponse(QuestionBank entity);

    /**
     * Update QuestionBank entity from UpdateQuestionBankRequest
     * Only updates non-null fields from the request
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget QuestionBank entity, UpdateQuestionBankRequest request, UUID updatedBy);

    /**
     * Partial update - only update specific fields
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "questionContent", ignore = true)
    @Mapping(target = "questionType", ignore = true)
    @Mapping(target = "difficultyLevel", ignore = true)
    void updateBasicInfo(@MappingTarget QuestionBank entity, UpdateQuestionBankRequest request, UUID updatedBy);


}
