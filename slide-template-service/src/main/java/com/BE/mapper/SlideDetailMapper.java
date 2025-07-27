package com.BE.mapper;

import com.BE.model.entity.SlideDetail;
import com.BE.model.response.SlideDetailResponse;
import org.mapstruct.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

@Mapper(componentModel = "spring")
public interface SlideDetailMapper {

    // Convert entity → response
    @Mapping(source = "slideTemplate.id", target = "slideTemplateId")
    @Mapping(source = "slideTemplate.name", target = "slideTemplateName")
    @Mapping(source = "slideData", target = "slideData", qualifiedByName = "stringToObject")
    SlideDetailResponse toResponse(SlideDetail entity);

    @Named("stringToObject")
    default Object stringToObject(String slideDataJson) {
        if (slideDataJson == null || slideDataJson.trim().isEmpty()) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(slideDataJson, Object.class);
        } catch (Exception e) {
            // Nếu không parse được, trả về string gốc
            return slideDataJson;
        }
    }
}
