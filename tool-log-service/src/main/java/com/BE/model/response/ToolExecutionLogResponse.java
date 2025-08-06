package com.BE.model.response;

import com.BE.enums.ExecutionStatus;
import com.BE.enums.ToolCodeEnum;
import com.BE.enums.ToolTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Thông tin log của một lần gọi công cụ")
public class ToolExecutionLogResponse {

    @Schema(description = "ID log", example = "1")
    Long id;

    @Schema(description = "ID người dùng gọi tool", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    UUID userId;

    @Schema(description = "ID công cụ được gọi", example = "2")
    UUID toolId;

    @Schema(  description = "Danh sách ID của các bài học được gọi",
            example = "[123, 456, 789]")
    List<Long> lessonIds;

    Long academicYearId;

    Long resultId;

    @Schema(description = "Mã định danh cố định của tool, dùng để mapping với FastAPI", example = "LESSON_PLAN, SLIDE_GENERATOR, EXAM_CREATOR")
    ToolCodeEnum code;

    ExecutionStatus status;

    @Schema(description = "Loại công cụ", example = "EXTERNAL")
    ToolTypeEnum toolType;

    @Schema(description = "Số lượng token bị trừ mỗi khi người dùng gọi một truy vấn", example = "10")
    Integer tokenUsed;

    @Schema(description = "Input JSON gửi đi", example = "{\"text\": \"Xin chào\"}")
    Map<String, Object> input;

    @Schema(description = "Output JSON trả về", example = "{\"translated\": \"Hello\"}")
    Map<String, Object> output;

    @Schema(description = "Thời gian tạo", example = "2025-07-10T15:04:23")
    LocalDateTime createdAt;

    @Schema(description = "Thời gian chỉnh sữa", example = "2025-07-10T15:04:23")
    LocalDateTime updatedAt;
}
