package com.BE.model.request;

import com.BE.enums.ToolTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @NotNull
    @Schema(description = "ID của người dùng thực hiện", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    UUID userId;

    @NotNull
    @Schema(description = "ID của công cụ được gọi", example = "123")
    Long toolId;

    @NotNull
    @Schema(description = "Loại công cụ", example = "EXTERNAL")
    ToolTypeEnum toolType;

    @Schema(description = "Input gốc gửi đến tool", example = "{\"text\": \"Xin chào\"}")
    String inputJson;

    @Schema(description = "Output tool trả về", example = "{\"translated\": \"Hello\"}")
    String outputJson;
}
