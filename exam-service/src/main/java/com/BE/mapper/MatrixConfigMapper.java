package com.BE.mapper;

import com.BE.model.entity.MatrixConfig;
import com.BE.model.request.MatrixConfigRequest;
import com.BE.model.response.MatrixConfigResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MatrixConfigMapper {

    MatrixConfig toEntity(MatrixConfigRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget MatrixConfig entity, MatrixConfigRequest request);

    MatrixConfigResponse toResponse(MatrixConfig entity);

    List<MatrixConfigResponse> toResponseList(List<MatrixConfig> entities);
}