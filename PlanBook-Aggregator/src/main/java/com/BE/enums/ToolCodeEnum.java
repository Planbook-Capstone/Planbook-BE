package com.BE.enums;

public enum ToolCodeEnum {
    LESSON_PLAN,
    SLIDE_GENERATOR,
    EXAM_CREATOR,
    MANUAL_EXAM_CREATOR,
    QUIZ_GAME,
    EXAM_GRADING,
    FORMU_LENS;

    public static String toVietnamese(ToolCodeEnum code) {
        return switch (code) {
            case LESSON_PLAN -> "Tạo giáo án từ AI";
            case SLIDE_GENERATOR -> "Tạo slide bài giảng từ AI";
            case EXAM_CREATOR -> "Tạo đề kiểm tra từ AI";
            case MANUAL_EXAM_CREATOR -> "Trộn đề kiểm tra";
            case QUIZ_GAME -> "Tạo bài thi luyện tập online";
            case FORMU_LENS -> "Nhận diện công thức";
            case EXAM_GRADING -> "Chấm điểm thi trắc nghiệm";
        };
    }
}
