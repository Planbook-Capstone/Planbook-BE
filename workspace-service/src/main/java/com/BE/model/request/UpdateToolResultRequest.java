package com.BE.model.request;

import com.BE.enums.ToolResultStatus;
import com.BE.enums.ToolResultType;
import com.BE.exception.EnumValidator;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho việc cập nhật ToolResult
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateToolResultRequest {

    @Size(max = 255, message = "name không được vượt quá 255 ký tự")
    private String name;

    @Size(max = 1000, message = "description không được vượt quá 1000 ký tự")
    private String description;

    private Object data;

    @EnumValidator(enumClass = ToolResultStatus.class, message = "status phải là một trong các giá trị: DRAFT, PUBLISHED, ARCHIVED, DELETED, IN_REVIEW, APPROVED, REJECTED")
    private ToolResultStatus status;
}
