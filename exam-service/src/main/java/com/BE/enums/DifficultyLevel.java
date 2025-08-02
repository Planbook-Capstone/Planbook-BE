package com.BE.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(
    description = """
        Difficulty level enum representing the cognitive complexity of questions.
        Based on Bloom's taxonomy for educational assessment.
        """,
    example = "BIET"
)
public enum DifficultyLevel {
    @Schema(description = "Knowledge level: recall of facts, terms, basic concepts")
    KNOWLEDGE("KNOWLEDGE", "Knowledge", 1, "Recall of facts, terms, basic concepts"),

    @Schema(description = "Comprehension level: understanding meaning, interpretation")
    COMPREHENSION("COMPREHENSION", "Comprehension", 2, "Understanding meaning, interpretation, explanation"),

    @Schema(description = "Application level: using knowledge in new situations")
    APPLICATION("APPLICATION", "Application", 3, "Applying knowledge to new situations"),

    @Schema(description = "Higher-order application: analysis, synthesis, evaluation")
    ANALYSIS("ANALYSIS", "Analysis", 4, "Analysis, synthesis, evaluation");

    private final String code;
    private final String name;
    private final int level;
    private final String description;

    DifficultyLevel(String code, String name, int level, String description) {
        this.code = code;
        this.name = name;
        this.level = level;
        this.description = description;
    }

    /**
     * Check if this is a basic level (Knowledge, Comprehension)
     */
    public boolean isBasicLevel() {
        return this == KNOWLEDGE || this == COMPREHENSION;
    }

    /**
     * Check if this is an application level (Application, Analysis)
     */
    public boolean isApplicationLevel() {
        return this == APPLICATION || this == ANALYSIS;
    }

    /**
     * Check if this is the highest difficulty level
     */
    public boolean isHighestLevel() {
        return this == ANALYSIS;
    }

    /**
     * Get difficulty score for weighting questions
     */
    public double getDifficultyScore() {
        return switch (this) {
            case KNOWLEDGE -> 1.0;
            case COMPREHENSION -> 1.2;
            case APPLICATION -> 1.5;
            case ANALYSIS -> 2.0;
        };
    }

    /**
     * Compare difficulty levels
     */
    public boolean isHarderThan(DifficultyLevel other) {
        return this.level > other.level;
    }

    /**
     * Compare difficulty levels
     */
    public boolean isEasierThan(DifficultyLevel other) {
        return this.level < other.level;
    }
}
