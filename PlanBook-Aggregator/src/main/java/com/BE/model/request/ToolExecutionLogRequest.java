package com.BE.model.request;

import com.BE.enums.ToolCodeEnum;
import com.BE.enums.ToolTypeEnum;
import com.BE.exception.EnumValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;
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

    @NotNull(message = "ID sách không được để trống")
    @Schema(description = "ID của sách được gọi", example = "456", required = true)
    Long bookId;

    @Schema(
            description = "Danh sách ID của các bài học được gọi",
            example = "[123, 456, 789]",
            required = true
    )
    @NotNull(message = "Danh sách ID bài học không được để trống")
    List<Long> lessonIds;

    @Schema(description = "WorkspaceId", example = "456", required = true)
    @NotNull(message = "WorkspaceId không được để trống")
    Long workspaceId;

    Long resultId;

    @NotNull(message = "Mã tool không được để trống")
    @EnumValidator(enumClass = ToolCodeEnum.class, message = "Code không hợp lệ")
    @Schema(description = "Mã định danh cố định của tool, dùng để mapping với FastAPI", example = "LESSON_PLAN, SLIDE_GENERATOR, EXAM_CREATOR", required = true)
    ToolCodeEnum code;

    @NotNull(message = "Loại công cụ không được để trống")
    @Schema(description = "Loại công cụ", example = "EXTERNAL", required = true)
    ToolTypeEnum toolType;

    @NotNull(message = "Số lượng token bị trừ mỗi khi người dùng gọi một truy vấn (query).")
    @Min(value = 1, message = "Giá trị tối thiểu: 1.")
    @Max(value = 1000, message = "Giá trị tối đa đề xuất: 1000.")
    Integer tokenUsed;

    @NotNull(message = "Input không được để trống")
    @Schema(description = "Input gốc gửi đến tool", example = "{\"text\": \"Xin chào\"}")
    Map<String, Object> input;

    @Schema(description = "Output tool trả về", example = "{\"translated\": \"Hello\"}")
    Map<String, Object> output;


}
