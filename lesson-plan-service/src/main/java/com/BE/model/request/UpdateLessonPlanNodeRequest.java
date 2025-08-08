package com.BE.model.request;

import com.BE.enums.NodeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

/**
 * Request DTO for updating a lesson plan node
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Yêu cầu cập nhật node trong cây giáo án")
public class UpdateLessonPlanNodeRequest {

    @Schema(description = "Tiêu đề của node", example = "Mục tiêu bài học đã cập nhật")
    String title;

    @Schema(description = "Nội dung của node", example = "Học sinh hiểu được khái niệm nâng cao")
    String content;

    @Schema(description = "Nội dung mô tả của node", example = "Học sinh hiểu được khái niệm cơ bản")
    String description;

    @Schema(description = "Loại input", example = "INPUT, UPLOAD", required = true)
    String fieldType;

    @Schema(description = "Loại node", example = "SUBSECTION")
    NodeType type;

    @Schema(description = "Thứ tự sắp xếp", example = "2")
    Integer orderIndex;

    @Schema(description = "Metadata bổ sung dưới dạng JSON")
    Map<String, Object> metadata;
}
