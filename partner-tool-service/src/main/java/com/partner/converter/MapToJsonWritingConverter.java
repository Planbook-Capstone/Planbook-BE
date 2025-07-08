package com.partner.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.HashMap;
import java.util.Map;

@WritingConverter
public class MapToJsonWritingConverter implements Converter<Map<String, Object>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convert(Map<String, Object> source) {
        try {
            return objectMapper.writeValueAsString(source);
        } catch (Exception e) {
            throw new RuntimeException("Serialization error", e);
        }
    }
}
