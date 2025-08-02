package com.BE.model.request;

import com.BE.enums.DifficultyLevel;
import com.BE.enums.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new question bank entry")
public class CreateQuestionBankRequest {

    @Schema(description = "Lesson ID that this question belongs to", example = "1", required = true)
    private Long lessonId;

    @Schema(description = "Question type (part)", example = "PART_I", required = true)
    private QuestionType questionType;

    @Schema(description = "Difficulty level", example = "KNOWLEDGE", required = true)
    private DifficultyLevel difficultyLevel;

    @Schema(description = """
            Question content in JSON format based on question type.

            **Common fields for all types:**
            - question: The question text (required)
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
            required = true,
            example = """
                {
                    "question": "Đơn vị đo khối lượng nguyên tử là gì?",
                    "image": "https://example.com/images/atomic-mass.png",
                    "options": {
                        "A": "kg",
                        "B": "g",
                        "C": "amu",
                        "D": "Å"
                    },
                    "answer": "C"
                }
                """)
    private Map<String, Object> questionContent;

    @Schema(description = "Explanation for the answer", example = "Đơn vị khối lượng nguyên tử (amu) được sử dụng để đo khối lượng của các nguyên tử và phân tử")
    private String explanation;

    @Schema(description = "Reference source", example = "Sách giáo khoa Hóa học 10 - Trang 25")
    private String referenceSource;
}
