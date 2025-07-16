package com.BE.mapper;

import com.BE.model.entity.SlideTemplate;
import com.BE.model.request.SlideTemplateRequest;
import com.BE.model.response.SlideTemplateResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SlideTemplateMapper {

    // Convert entity → response
    SlideTemplateResponse toResponse(SlideTemplate entity);

    // Convert request → entity (tạo mới)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SlideTemplate toEntity(SlideTemplateRequest request);

    // Cập nhật entity từ request (update use case)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(SlideTemplateRequest request, @MappingTarget SlideTemplate entity);
}
