package com.BE.model.request;

import com.BE.enums.DifficultyLevel;
import com.BE.enums.QuestionType;
import com.BE.enums.QuestionBankVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update an existing question bank entry")
public class UpdateQuestionBankRequest {

    @Schema(description = "Lesson ID that this question belongs to", example = "2")
    private Long lessonId;

    @Schema(description = "Question type (part)", example = "PART_II")
    private QuestionType questionType;

    @Schema(description = "Difficulty level", example = "COMPREHENSION")
    private DifficultyLevel difficultyLevel;

    @Schema(description = """
            Updated question content in JSON format based on question type.

            **Common fields for all types:**
            - question: The question text
            - image: URL to question image (optional)

            **PART_I (Multiple Choice):**
            - options: Object with A, B, C, D keys and their values
            - answer: The correct option key (A, B, C, or D)

            **PART_II (True/False):**
            - statements: Object with statement keys and their text
            - answers: Object with statement keys and true/false values

            **PART_III (Short Answer):**
            - answer: The expected answer text
            - keywords: Array of keywords for grading (optional)
            """,
            example = """
                {
                    "question": "Đơn vị chuẩn để đo khối lượng nguyên tử là gì?",
                    "image": "https://example.com/images/updated-atomic-mass.png",
                    "options": {
                        "A": "kilogram (kg)",
                        "B": "gram (g)",
                        "C": "đơn vị khối lượng nguyên tử (amu)",
                        "D": "angstrom (Å)"
                    },
                    "answer": "C"
                }
                """)
    private Map<String, Object> questionContent;

    @Schema(description = "Updated explanation for the answer",
            example = "Đơn vị khối lượng nguyên tử (amu) được định nghĩa bằng 1/12 khối lượng của nguyên tử carbon-12")
    private String explanation;

    @Schema(description = "Updated reference source", example = "Sách giáo khoa Hóa học 10 - Trang 25-27")
    private String referenceSource;

    @Schema(description = "Visibility level of the question bank", example = "PUBLIC")
    private QuestionBankVisibility visibility;
}
