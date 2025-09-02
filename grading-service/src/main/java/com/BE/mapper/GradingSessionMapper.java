package com.BE.mapper;

import com.BE.model.entity.GradingSession;
import com.BE.model.entity.OmrTemplate;
import com.BE.model.request.GradingSessionRequest;
import com.BE.model.request.GradingSessionUpdateRequest;
import com.BE.model.request.OmrTemplateRequest;
import com.BE.model.response.GradingSessionResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {OmrTemplateMapper.class, AnswerSheetKeyMapper.class})
public interface GradingSessionMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "omrTemplate", ignore = true), // Handled in service
        @Mapping(target = "answerSheetKeys", ignore = true),
        @Mapping(target = "studentSubmissions", ignore = true),
        @Mapping(target = "createdAt", ignore = true),
        @Mapping(target = "updatedAt", ignore = true)
    })
    GradingSession toEntity(GradingSessionRequest request);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget GradingSession gradingSession, GradingSessionUpdateRequest gradingSessionUpdateRequest);
    @Mapping(source = "studentSubmissions", target = "totalSubmissions")
    GradingSessionResponse toResponse(GradingSession gradingSession);

}

