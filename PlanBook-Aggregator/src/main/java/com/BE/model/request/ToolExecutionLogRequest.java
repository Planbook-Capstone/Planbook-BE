package com.BE.model.request;

import com.BE.enums.ToolTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ToolExecutionLogRequest {


    @NotNull(message = "ID người dùng không được để trống")
    @Schema(description = "ID của người dùng thực hiện", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479", required = true)
    UUID userId;

    @NotNull(message = "ID công cụ không được để trống")
    @Schema(description = "ID của công cụ được gọi", example = "123", required = true)
    UUID toolId;

    @NotNull(message = "ID bài học không được để trống")
    @Schema(description = "ID của bài được gọi", example = "456", required = true)
    Long lessonId;

    @NotBlank(message = "Tên công cụ không được để trống")
    @Schema(description = "Tên của công cụ", example = "Lesson Plan Generator")
    String toolName;

    @NotNull(message = "Loại công cụ không được để trống")
    @Schema(description = "Loại công cụ", example = "EXTERNAL", required = true)
    ToolTypeEnum toolType;

    @NotBlank(message = "InputJson không được để trống")
    @Schema(description = "Input gốc gửi đến tool", example = "{\"text\": \"Xin chào\"}")
    String inputJson;

    @Schema(description = "Output tool trả về", example = "{\"translated\": \"Hello\"}")
    String outputJson;
}
