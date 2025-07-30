package com.BE.enums;

/**
 * Enum định nghĩa các trường có thể sắp xếp cho ToolResult
 */
public enum ToolResultSortBy {
    ID("id"),
    NAME("name"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt"),
    TYPE("type"),
    STATUS("status");

    private final String fieldName;

    ToolResultSortBy(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
