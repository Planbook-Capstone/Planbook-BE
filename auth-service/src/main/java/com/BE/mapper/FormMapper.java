package com.BE.mapper;

import com.BE.model.entity.Form;
import com.BE.model.response.FormResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class FormMapper {

    @Autowired
    protected ObjectMapper objectMapper;

    @Mapping(target = "formData", source = "formDefinition", qualifiedByName = "jsonStringToJsonNode")
    public abstract FormResponse toFormResponse(Form form);

    public abstract List<FormResponse> toFormResponseList(List<Form> forms);

    @Named("jsonStringToJsonNode")
    protected JsonNode jsonStringToJsonNode(String formDefinition) {
        try {
            if (formDefinition == null) {
                return null;
            }
            return objectMapper.readTree(formDefinition);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing form definition JSON", e);
        }
    }
}
