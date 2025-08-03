package com.BE.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(
    description = """
        Difficulty level enum representing the cognitive complexity of questions.
        Based on Bloom's taxonomy for educational assessment.
        """,
    example = "KNOWLEDGE"
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

}
