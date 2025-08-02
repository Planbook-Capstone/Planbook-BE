package com.BE.mapper;

import com.BE.model.entity.AcademicResource;
import com.BE.model.request.AcademicResourceCreateRequest;
import com.BE.model.request.AcademicResourceUpdateRequest;
import com.BE.model.response.AcademicResourceInternalResponse;
import com.BE.model.response.AcademicResourceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AcademicResourceMapper {

    AcademicResource toEntity(AcademicResourceCreateRequest request);

    AcademicResourceResponse toResponse(AcademicResource entity);

    AcademicResourceInternalResponse toInternalResponse(AcademicResource entity);

    void updateAcademicResource(@MappingTarget AcademicResource entity, AcademicResourceUpdateRequest request);


}
