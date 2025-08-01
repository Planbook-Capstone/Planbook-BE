package com.BE.model.request;

import com.BE.enums.SortDirection;
import com.BE.enums.ToolResultSortBy;
import com.BE.enums.ToolResultStatus;
import com.BE.enums.ToolResultType;
import com.BE.exception.EnumValidator;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO cho filter request của ToolResult API - Lọc linh hoạt kết hợp phân trang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Yêu cầu lọc danh sách kết quả công cụ AI")
public class ToolResultFilterRequest {

    @Schema(description = "ID người dùng", example = "0d29b45a-5d6a-44e2-b58d-d7aa5180cb0f")
    private UUID userId;

    @Schema(description = "ID workspace", example = "101")
    private Long workspaceId;

    @EnumValidator(enumClass = ToolResultType.class, message = "type phải là một trong các giá trị hợp lệ")
    @Schema(description = "Loại kết quả công cụ", example = "LESSON_PLAN")
    private ToolResultType type;

    @EnumValidator(enumClass = ToolResultStatus.class, message = "status phải là một trong các giá trị hợp lệ")
    @Schema(description = "Trạng thái kết quả", example = "ARCHIVED")
    private ToolResultStatus status;

    @Schema(description = "ID của template nếu có", example = "789")
    private Long templateId;

    @ArraySchema(schema = @Schema(description = "Danh sách ID bài học liên kết", example = "456"), minItems = 1)
    private List<Long> lessonIds;

    @Schema(description = "Tìm kiếm tên gần đúng (không phân biệt hoa thường)", example = "bài 1")
    private String nameContains;

    @Schema(description = "Tìm kiếm mô tả gần đúng", example = "phiên bản nháp giáo án")
    private String descriptionContains;

    @Schema(description = "Số trang (bắt đầu từ 1)", example = "1")
    private Integer page = 1;

    @Schema(description = "Kích thước trang", example = "10")
    private Integer size = 10;

    @EnumValidator(enumClass = ToolResultSortBy.class, message = "sortBy phải là một trong các giá trị hợp lệ")
    @Schema(description = "Trường sắp xếp")
    private ToolResultSortBy sortBy = ToolResultSortBy.CREATED_AT;

    @EnumValidator(enumClass = SortDirection.class, message = "sortDirection phải là ASC hoặc DESC")
    @Schema(description = "Hướng sắp xếp (asc/desc)")
    private SortDirection sortDirection = SortDirection.DESC;

    // === Convenience methods ===

    public boolean hasUserId() {
        return userId != null;
    }

    public boolean hasWorkspaceId() {
        return workspaceId != null;
    }

    public boolean hasType() {
        return type != null;
    }

    public boolean hasStatus() {
        return status != null;
    }

    public boolean hasTemplateId() {
        return templateId != null;
    }

    public boolean hasNameContains() {
        return nameContains != null && !nameContains.trim().isEmpty();
    }

    public boolean hasDescriptionContains() {
        return descriptionContains != null && !descriptionContains.trim().isEmpty();
    }

    public boolean hasLessonIds() {
        return lessonIds != null && !lessonIds.isEmpty();
    }

    public boolean hasAnyFilter() {
        return hasUserId() || hasWorkspaceId() || hasType() || hasStatus()
                || hasTemplateId() || hasNameContains() || hasDescriptionContains()
                || hasLessonIds();
    }
}
