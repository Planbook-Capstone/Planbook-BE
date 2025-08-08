package com.BE.model.response;

import com.BE.enums.NodeType;
import com.BE.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

/**
 * DTO for LessonPlanNode with tree structure support
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Cấu trúc node trong cây giáo án")
public class LessonPlanNodeDTO {

    @Schema(description = "ID của node", example = "1")
    Long id;

    @Schema(description = "ID của giáo án", example = "101")
    Long lessonPlanTemplateId;

    @Schema(description = "ID của node cha", example = "2")
    Long parentId;

    @Schema(description = "Tiêu đề của node", example = "Mục tiêu bài học")
    String title;

    @Schema(description = "Nội dung của node", example = "Học sinh hiểu được khái niệm cơ bản")
    String content;

    @Schema(description = "Nội dung mô tả của node", example = "Học sinh hiểu được khái niệm cơ bản")
    String description;

    @Schema(description = "Loại input", example = "INPUT, UPLOAD")
    String fieldType;

    @Schema(description = "Loại node", example = "SECTION")
    NodeType type;

    @Schema(description = "Thứ tự sắp xếp", example = "1")
    Integer orderIndex;

    @Schema(description = "Metadata bổ sung dưới dạng JSON")
    Map<String, Object> metadata;

    @Schema(description = "Trạng thái của node", example = "ACTIVE")
    Status status;

    @Schema(description = "Danh sách node con")
    List<LessonPlanNodeDTO> children;
}
