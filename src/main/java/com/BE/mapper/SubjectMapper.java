package com.BE.mapper;

import com.BE.model.entity.Subject;
import com.BE.model.request.SubjectRequest;
import com.BE.model.response.SubjectResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubjectMapper {

    Subject toSubject(SubjectRequest subjectRequest);

    @Mapping(target = "grade", source = "grade")
    SubjectResponse toSubjectResponse(Subject subject);
}
