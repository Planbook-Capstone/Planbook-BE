package com.BE.mapper;

import com.BE.model.entity.AnswerSheetKey;
import com.BE.model.request.AnswerSheetKeyRequest;
import com.BE.model.response.AnswerSheetKeyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AnswerSheetKeyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "gradingSession", ignore = true) // Handled in service
    @Mapping(target = "studentSubmissions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AnswerSheetKey toEntity(AnswerSheetKeyRequest request);

    @Mapping(source = "gradingSession.id", target = "gradingSessionId")
    @Mapping(source = "studentSubmissions", target = "totalSubmissions")
    AnswerSheetKeyResponse toResponse(AnswerSheetKey answerSheetKey);

    default Integer map(java.util.List<com.BE.model.entity.StudentSubmission> submissions) {
        return submissions != null ? submissions.size() : 0;
    }
}

