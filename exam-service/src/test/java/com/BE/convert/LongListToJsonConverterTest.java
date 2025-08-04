package com.BE.convert;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class LongListToJsonConverterTest {

    private LongListToJsonConverter converter;

    @BeforeEach
    void setUp() {
        converter = new LongListToJsonConverter();
    }

    @Test
    void testConvertToDatabaseColumn_WithValidList() {
        // Given
        List<Long> lessonIds = Arrays.asList(1L, 2L, 3L);

        // When
        String result = converter.convertToDatabaseColumn(lessonIds);

        // Then
        assertEquals("[1,2,3]", result);
    }

    @Test
    void testConvertToDatabaseColumn_WithEmptyList() {
        // Given
        List<Long> lessonIds = new ArrayList<>();

        // When
        String result = converter.convertToDatabaseColumn(lessonIds);

        // Then
        assertEquals("[]", result);
    }

    @Test
    void testConvertToDatabaseColumn_WithNullList() {
        // Given
        List<Long> lessonIds = null;

        // When
        String result = converter.convertToDatabaseColumn(lessonIds);

        // Then
        assertEquals("[]", result);
    }

    @Test
    void testConvertToEntityAttribute_WithValidJson() {
        // Given
        String json = "[1,2,3]";

        // When
        List<Long> result = converter.convertToEntityAttribute(json);

        // Then
        assertEquals(3, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
        assertTrue(result.contains(3L));
    }

    @Test
    void testConvertToEntityAttribute_WithEmptyJson() {
        // Given
        String json = "[]";

        // When
        List<Long> result = converter.convertToEntityAttribute(json);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void testConvertToEntityAttribute_WithNullJson() {
        // Given
        String json = null;

        // When
        List<Long> result = converter.convertToEntityAttribute(json);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void testConvertToEntityAttribute_WithBlankJson() {
        // Given
        String json = "   ";

        // When
        List<Long> result = converter.convertToEntityAttribute(json);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void testRoundTrip() {
        // Given
        List<Long> originalList = Arrays.asList(10L, 20L, 30L);

        // When
        String json = converter.convertToDatabaseColumn(originalList);
        List<Long> convertedBack = converter.convertToEntityAttribute(json);

        // Then
        assertEquals(originalList.size(), convertedBack.size());
        assertTrue(convertedBack.containsAll(originalList));
    }
}
