package com.BE.model.request;

import com.BE.enums.NodeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

/**
 * Request DTO for creating a new lesson plan node
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Yêu cầu tạo node mới trong cây giáo án")
public class CreateLessonPlanNodeRequest {

    @NotNull(message = "ID giáo án không được để trống")
    @Schema(description = "ID của giáo án", example = "101", required = true)
    Long lessonPlanTemplateId;

    @Schema(description = "ID của node cha (null nếu là node gốc)", example = "2")
    Long parentId;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Schema(description = "Tiêu đề của node", example = "Mục tiêu bài học", required = true)
    String title;

    @Schema(description = "Nội dung của node", example = "Học sinh hiểu được khái niệm cơ bản")
    String content;

    @NotNull(message = "Loại node không được để trống")
    @Schema(description = "Loại node", example = "SECTION", required = true)
    NodeType type;


    @Schema(description = "Loại input", example = "INPUT, UPLOAD", required = true)
    String fieldType;

    @NotNull(message = "Thứ tự sắp xếp không được để trống")
    @Schema(description = "Thứ tự sắp xếp", example = "1", required = true)
    Integer orderIndex;

    @Schema(description = "Metadata bổ sung dưới dạng JSON")
    Map<String, Object> metadata;
}
