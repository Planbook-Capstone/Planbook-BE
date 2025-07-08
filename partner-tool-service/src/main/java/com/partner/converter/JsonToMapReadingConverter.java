package com.partner.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.HashMap;
import java.util.Map;

@ReadingConverter
public class JsonToMapReadingConverter implements Converter<String, Map<String, Object>> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> convert(String source) {
        try {
            return objectMapper.readValue(source, HashMap.class);
        } catch (Exception e) {
            throw new RuntimeException("Deserialization error", e);
        }
    }
}
