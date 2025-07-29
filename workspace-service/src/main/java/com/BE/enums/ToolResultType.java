package com.BE.enums;

/**
 * Enum định nghĩa các loại kết quả công cụ AI có thể tạo ra
 */
public enum ToolResultType {
    LESSON_PLAN("Giáo án"),
    SLIDE("Slide bài giảng"),
    EXAM("Đề kiểm tra"),
    QUIZ("Câu hỏi trắc nghiệm"),
    WORKSHEET("Bài tập"),
    ASSIGNMENT("Bài tập về nhà"),
    RUBRIC("Thang đánh giá"),
    CURRICULUM("Chương trình học"),
    ACTIVITY("Hoạt động học tập"),
    ASSESSMENT("Đánh giá"),
    OTHER("Khác");

    private final String displayName;

    ToolResultType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
