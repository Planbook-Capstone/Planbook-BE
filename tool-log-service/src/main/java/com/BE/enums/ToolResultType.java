package com.BE.enums;

/**
 * Enum định nghĩa các loại kết quả công cụ AI có thể tạo ra
 */
public enum ToolResultType {
    LESSON_PLAN("Giáo án"),
    SLIDE("Slide bài giảng"),
    EXAM("Đề kiểm tra"),
    FORMU_LENS("Tra cứu tài nguyên");

    private final String displayName;

    ToolResultType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
