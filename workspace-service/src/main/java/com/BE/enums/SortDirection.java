package com.BE.enums;

/**
 * Enum định nghĩa hướng sắp xếp
 */
public enum SortDirection {
    ASC("Tăng dần"),
    DESC("Giảm dần");

    private final String displayName;

    SortDirection(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
