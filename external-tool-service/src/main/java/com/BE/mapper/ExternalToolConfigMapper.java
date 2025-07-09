package com.BE.mapper;

import com.BE.model.entity.ExternalToolConfig;
import com.BE.model.request.ExternalToolConfigRequest;
import com.BE.model.response.ExternalToolConfigResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ExternalToolConfigMapper {
    ExternalToolConfig toEntity(ExternalToolConfigRequest dto);
    ExternalToolConfigResponse toResponse(ExternalToolConfig entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget ExternalToolConfig target, ExternalToolConfigRequest source);
}