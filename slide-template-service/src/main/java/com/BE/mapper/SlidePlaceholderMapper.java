package com.BE.mapper;

import com.BE.model.entity.SlidePlaceholder;
import com.BE.model.request.SlidePlaceholderRequest;
import com.BE.model.response.SlidePlaceholderResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SlidePlaceholderMapper {

    // Convert entity → response
    SlidePlaceholderResponse toResponse(SlidePlaceholder entity);

    // Convert request → entity (tạo mới)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SlidePlaceholder toEntity(SlidePlaceholderRequest request);

    // Cập nhật entity từ request (update use case)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(SlidePlaceholderRequest request, @MappingTarget SlidePlaceholder entity);
}
