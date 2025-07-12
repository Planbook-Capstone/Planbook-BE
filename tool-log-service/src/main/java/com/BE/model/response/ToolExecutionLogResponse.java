package com.BE.model.response;

import com.BE.enums.ToolTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
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
    Long toolId;

    @Schema(description = "ID của bài được gọi", example = "3")
    Long lessonId;

    @Schema(description = "Tên của công cụ", example = "Lesson Plan Generator")
    String toolName;

    @Schema(description = "Loại công cụ", example = "EXTERNAL")
    ToolTypeEnum toolType;

    @Schema(description = "Input JSON gửi đi", example = "{\"text\": \"Xin chào\"}")
    String inputJson;

    @Schema(description = "Output JSON trả về", example = "{\"translated\": \"Hello\"}")
    String outputJson;

    @Schema(description = "Thời gian tạo", example = "2025-07-10T15:04:23")
    LocalDateTime createdAt;
}
