package com.BE.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemBankQuestionDTO {

    @Schema(description = "ID câu hỏi", example = "32")
    private Long id;

    @Schema(description = "Loại phần (part) câu hỏi, ví dụ: PART_III", example = "PART_III")
    private String questionType;

    @Schema(description = "Mô tả loại phần câu hỏi", example = "Phần III - Câu trả lời ngắn")
    private String questionTypeDescription;

    @Schema(description = "Mức độ khó của câu hỏi", example = "KNOWLEDGE")
    private String difficultyLevel;

    @Schema(description = "Mô tả độ khó", example = "Recall of facts, terms, basic concepts")
    private String difficultyLevelDescription;

    @Schema(description = "Nội dung câu hỏi bao gồm question, answer, options,...")
    private Map<String, Object> questionContent;

    @Schema(description = "Lời giải/giải thích", example = "...")
    private String explanation;

    @Schema(description = "Nguồn tham khảo", example = "THPT CHUYÊN LONG AN")
    private String referenceSource;

    @Schema(description = "Danh sách ID bài học liên quan", example = "[8]")
    private List<Long> lessonIds;

    @Schema(description = "Trạng thái hiển thị", example = "PUBLIC")
    private String visibility;

    @Schema(description = "Người tạo", example = "84806e6f-6e5f-4f00-9c2f-e228b8c30cf7")
    private String createdBy;

    @Schema(description = "Người cập nhật gần nhất")
    private String updatedBy;

    @Schema(description = "Thời điểm tạo", example = "2025-08-06T18:12:04")
    private LocalDateTime createdAt;

    @Schema(description = "Thời điểm cập nhật", example = "2025-08-06T18:12:04")
    private LocalDateTime updatedAt;
}
