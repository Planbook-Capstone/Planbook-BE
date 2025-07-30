package com.BE.model.request;

import com.BE.enums.ToolResultStatus;
import com.BE.enums.ToolResultType;
import com.BE.exception.EnumValidator;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO cho việc tạo mới ToolResult
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateToolResultRequest {

    @NotNull(message = "userId không được để trống")
    private UUID userId;

    @NotNull(message = "workspaceId không được để trống")
    private Long workspaceId;

    @NotNull(message = "type không được để trống")
    @EnumValidator(enumClass = ToolResultType.class, message = "type phải là một trong các giá trị: LESSON_PLAN, SLIDE, EXAM, QUIZ, WORKSHEET, ASSIGNMENT, RUBRIC, CURRICULUM, ACTIVITY, ASSESSMENT, OTHER")
    private ToolResultType type;

    @Schema(
            description = "Danh sách ID của các bài học được gọi",
            example = "[123, 456, 789]",
            required = true
    )
    @NotNull(message = "Danh sách ID bài học không được để trống")
    private List<Long> lessonIds;

    @NotBlank(message = "name không được để trống")
    @Size(max = 255, message = "name không được vượt quá 255 ký tự")
    private String name;

    @Size(max = 1000, message = "description không được vượt quá 1000 ký tự")
    private String description;
    
    private Object data;

    @NotNull(message = "status không được để trống")
    @EnumValidator(enumClass = ToolResultStatus.class, message = "status phải là một trong các giá trị: DRAFT, PUBLISHED, ARCHIVED, DELETED, IN_REVIEW, APPROVED, REJECTED")
    private ToolResultStatus status;
}
