package com.BE.mapper;


import com.BE.model.entity.Grade;
import com.BE.model.request.GradeRequest;
import com.BE.model.response.GradeResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GradeMapper {

    Grade toGrade(GradeRequest gradeRequest);

    GradeResponse toGradeResponse(Grade grade);

}
