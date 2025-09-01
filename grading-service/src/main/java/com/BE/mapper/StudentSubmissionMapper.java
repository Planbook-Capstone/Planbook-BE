package com.BE.mapper;

import com.BE.model.entity.StudentSubmission;
import com.BE.model.response.StudentSubmissionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentSubmissionMapper {

    @Mapping(source = "gradingSession.id", target = "gradingSessionId")
    @Mapping(source = "answerSheetKey.id", target = "answerSheetKeyId")
    StudentSubmissionResponse toResponse(StudentSubmission studentSubmission);
}

