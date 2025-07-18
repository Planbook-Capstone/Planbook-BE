package com.BE.model.request;


import com.BE.enums.ToolTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class ToolExecuteRequest {

    @NotNull(message = "toolId không được để trống")
    @Schema(
            description = "ID của công cụ muốn gọi (BookType hoặc ExternalToolConfig)",
            example = "a1b2c3d4-e5f6-7890-1234-567890abcdef",
            required = true
    )
    private UUID toolId;

    @NotNull(message = "toolType không được để trống")
    @Schema(
            description = "Loại công cụ: BOOK_TYPE (gọi nội bộ) hoặc EXTERNAL_TOOL (gọi API ngoài)",
            example = "BOOK_TYPE",
            required = true
    )
    private ToolTypeEnum toolType;

    @NotNull(message = "lessonId không được để trống")
    @Schema(
            description = "ID của bài học mà tool đang xử lý",
            example = "1",
            required = true
    )
    private Long lesson_id;


    @NotNull(message = "Input không được để trống")
    @Schema(
            description = "Input gốc gửi đến tool",
            example = "{\"text\": \"Xin chào\"}"
    )
    private Map<String, Object> input;
}

