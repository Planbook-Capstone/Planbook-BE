package com.BE.convert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;

@Converter
public class LongListToJsonConverter implements AttributeConverter<List<Long>, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Long> attribute) {
        if (attribute == null) return "[]";
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Error converting list to JSON", e);
        }
    }

    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return new ArrayList<>();
        try {
            return mapper.readValue(dbData, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to list", e);
        }
    }
}
