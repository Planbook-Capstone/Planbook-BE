package com.BE.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(
    description = """
        Question type enum representing different parts of an exam.
        Each part has specific characteristics and scoring rules.
        """,
    example = "PART_I"
)
public enum QuestionType {
    @Schema(description = "Part I - Multiple choice questions with single correct answer")
    PART_I("PART_I", "Phần I - Câu trắc nghiệm nhiều phương án lựa chọn"),

    @Schema(description = "Part II - True/False questions with multiple statements")
    PART_II("PART_II", "Phần II - Câu trắc nghiệm đúng sai"),

    @Schema(description = "Part III - Essay questions requiring written answers")
    PART_III("PART_III", "Phần III - Câu trả lời ngắn");

    private final String code;
    private final String description;

    QuestionType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Check if this question type supports multiple choice options
     */
    public boolean hasOptions() {
        return this == PART_I;
    }

    /**
     * Check if this question type supports true/false statements
     */
    public boolean hasStatements() {
        return this == PART_II;
    }

    /**
     * Check if this question type is essay-based
     */
    public boolean isEssay() {
        return this == PART_III;
    }

    /**
     * Get the part name for JSON structure
     */
    public String getPartName() {
        return switch (this) {
            case PART_I -> "PHẦN I";
            case PART_II -> "PHẦN II";
            case PART_III -> "PHẦN III";
        };
    }

    /**
     * Get default title for this question type
     */
    public String getDefaultTitle() {
        return switch (this) {
            case PART_I -> "Câu trắc nghiệm nhiều phương án lựa chọn";
            case PART_II -> "Câu trắc nghiệm đúng sai";
            case PART_III -> "Câu trả lời ngắn";
        };
    }
}
