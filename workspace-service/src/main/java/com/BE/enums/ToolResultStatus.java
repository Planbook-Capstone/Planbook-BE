package com.BE.enums;

/**
 * Enum định nghĩa trạng thái của kết quả công cụ AI
 */
public enum ToolResultStatus {
    DRAFT("Bản nháp"),
    PUBLISHED("Đã xuất bản"),
    ARCHIVED("Đã lưu trữ"),
    DELETED("Đã xóa"),
    IN_REVIEW("Đang xem xét"),
    APPROVED("Đã phê duyệt"),
    REJECTED("Đã từ chối");

    private final String displayName;

    ToolResultStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
