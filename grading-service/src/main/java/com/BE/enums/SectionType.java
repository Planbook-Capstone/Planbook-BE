package com.BE.enums;

/**
 * Enum representing different types of sections in an exam
 */
public enum SectionType {
    MULTIPLE_CHOICE("MULTIPLE_CHOICE"),
    TRUE_FALSE("TRUE_FALSE"),
    FILL_IN_BLANK("FILL_IN_BLANK");

    private final String value;

    SectionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SectionType fromValue(String value) {
        for (SectionType type : SectionType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown SectionType: " + value);
    }
}
