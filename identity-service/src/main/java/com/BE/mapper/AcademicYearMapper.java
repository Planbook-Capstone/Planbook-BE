package com.BE.mapper;

import com.BE.model.entity.AcademicYear;
import com.BE.model.request.AcademicYearRequest;
import com.BE.model.response.AcademicYearResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface AcademicYearMapper {

    AcademicYear toEntity(AcademicYearRequest request);

    AcademicYearResponse toResponse(AcademicYear entity);

    void updateAcademicYear(@MappingTarget AcademicYear academicYear, AcademicYearRequest request);

}