package com.BE.mapper;

import com.BE.model.entity.OmrTemplate;
import com.BE.model.request.OmrTemplateRequest;
import com.BE.model.response.OmrTemplateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OmrTemplateMapper {
    OmrTemplate toEntity(OmrTemplateRequest request);
    OmrTemplateResponse toResponse(OmrTemplate omrTemplate);
    void updateEntityFromRequest(OmrTemplateRequest request, @MappingTarget OmrTemplate omrTemplate);
}

