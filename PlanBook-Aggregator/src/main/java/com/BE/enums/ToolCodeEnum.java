package com.BE.enums;

public enum ToolCodeEnum {
    LESSON_PLAN,
    SLIDE_GENERATOR,
    EXAM_CREATOR,
    MANUAL_EXAM_CREATOR,
    QUIZ_GAME,
    FORMU_LENS;

    public static String toVietnamese(ToolCodeEnum code) {
        return switch (code) {
            case LESSON_PLAN -> "Lập kế hoạch bài học";
            case SLIDE_GENERATOR -> "Tạo slide bài giảng";
            case EXAM_CREATOR -> "Tạo đề kiểm tra từ AI";
            case MANUAL_EXAM_CREATOR -> "Tạo đề từ kho";
            case QUIZ_GAME -> "Tạo bài thi trắc nghiệm";
            case FORMU_LENS -> "Nhận diện công thức";
        };
    }
}
