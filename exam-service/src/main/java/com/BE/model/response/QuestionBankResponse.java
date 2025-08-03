package com.BE.model.response;

import com.BE.enums.DifficultyLevel;
import com.BE.enums.QuestionType;
import com.BE.enums.QuestionBankVisibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing question bank information")
public class QuestionBankResponse {

    @Schema(description = "Question bank ID", example = "1")
    private Long id;

    @Schema(description = "Lesson ID that this question belongs to", example = "1")
    private Long lessonId;

    @Schema(description = "Question type (part)", example = "PART_I")
    private QuestionType questionType;

    @Schema(description = "Question type description", example = "Phần I - Câu trắc nghiệm nhiều phương án lựa chọn")
    private String questionTypeDescription;

    @Schema(description = "Difficulty level", example = "KNOWLEDGE")
    private DifficultyLevel difficultyLevel;

    @Schema(description = "Difficulty level description", example = "Knowledge")
    private String difficultyLevelDescription;

    @Schema(description = """
            Question content in JSON format with support for images.

            **Common fields for all types:**
            - question: The question text
            - image: URL to question image (optional)

            **Format varies by question type - see API documentation for details**
            """,
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

    @Schema(description = "Explanation for the answer", 
            example = "Đơn vị khối lượng nguyên tử (amu) được sử dụng để đo khối lượng của các nguyên tử và phân tử")
    private String explanation;

    @Schema(description = "Reference source", example = "Sách giáo khoa Hóa học 10 - Trang 25")
    private String referenceSource;

    @Schema(description = "Visibility level of the question bank", example = "PUBLIC")
    private QuestionBankVisibility visibility;

    @Schema(description = "ID of the user who created this question", example = "550e8400-e29b-41d4-a716-446655440002")
    private UUID createdBy;

    @Schema(description = "ID of the user who last updated this question", example = "550e8400-e29b-41d4-a716-446655440002")
    private UUID updatedBy;

    @Schema(description = "Creation timestamp", example = "2024-01-15T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-01-16T14:20:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
