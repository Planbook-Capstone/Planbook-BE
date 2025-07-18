package com.BE.mapper;

import com.BE.model.response.ExternalToolConfigPublicResponse;
import com.BE.model.response.ExternalToolConfigResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ToolAggregatorMapper {

    ExternalToolConfigPublicResponse toPublicResponse(ExternalToolConfigResponse entity);

}
