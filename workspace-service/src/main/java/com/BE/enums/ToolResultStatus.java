package com.BE.enums;

/**
 * Enum định nghĩa trạng thái của kết quả công cụ AI
 */
public enum ToolResultStatus {
    DRAFT("Bản nháp"),
    ARCHIVED("Đã lưu trữ"),
    DELETED("Đã xóa");

    private final String displayName;

    ToolResultStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
