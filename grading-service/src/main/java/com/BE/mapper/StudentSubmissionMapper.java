package com.BE.mapper;

import com.BE.model.entity.StudentSubmission;
import com.BE.model.request.StudentSubmissionRequest;
import com.BE.model.response.StudentSubmissionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentSubmissionMapper {
    StudentSubmissionResponse toResponse(StudentSubmission studentSubmission);

    StudentSubmission toEntity(StudentSubmissionRequest submissionRequest);
    
}

