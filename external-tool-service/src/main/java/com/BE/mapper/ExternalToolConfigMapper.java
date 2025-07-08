package com.BE.mapper;

import com.BE.model.entity.ExternalToolConfig;
import com.BE.model.request.ExternalToolConfigRequest;
import com.BE.model.response.ExternalToolConfigResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExternalToolConfigMapper {
    ExternalToolConfig toEntity(ExternalToolConfigRequest dto);
    ExternalToolConfigResponse toResponse(ExternalToolConfig entity);
}