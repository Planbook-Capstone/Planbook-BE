package com.BE.mapper;

import com.BE.model.entity.WorkSpace;
import com.BE.model.request.WorkSpaceRequest;
import com.BE.model.response.WorkSpaceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface WorkSpaceMapper {
    WorkSpaceMapper INSTANCE = Mappers.getMapper(WorkSpaceMapper.class);

    @Mapping(target = "academicYearId", source = "academicYear.id")
    @Mapping(target = "authId", source = "auth.id")
    WorkSpaceResponse toResponse(WorkSpace entity);
}