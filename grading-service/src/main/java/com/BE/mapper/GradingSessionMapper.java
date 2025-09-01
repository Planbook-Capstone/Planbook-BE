package com.BE.mapper;

import com.BE.model.entity.GradingSession;
import com.BE.model.request.GradingSessionRequest;
import com.BE.model.response.GradingSessionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

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

    @Mapping(source = "studentSubmissions", target = "totalSubmissions")
    GradingSessionResponse toResponse(GradingSession gradingSession);

}

