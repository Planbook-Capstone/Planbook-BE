package com.BE.model.response;

import com.BE.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonResponse {

    @Schema(
            description = "ID duy nhất của bài học. Giá trị này được hệ thống tự động sinh.",
            type = "integer",
            format = "int64",
            example = "1"
    )
    long id;

    @Schema(
            description = "Tên của bài học.",
            type = "string",
            example = "Bài 1: Cấu tạo nguyên tử"
    )
    String name;

    @Schema(
            description = "Trạng thái hoạt động của bài học. Có thể là 'ACTIVE' hoặc 'INACTIVE'.",
            type = "string",
            allowableValues = { "ACTIVE", "INACTIVE" },
            example = "ACTIVE"
    )
    StatusEnum status;

    @Schema(
            description = "Thời điểm bài học được tạo lần đầu. Định dạng theo chuẩn ISO 8601.",
            type = "string",
            format = "date-time",
            example = "2025-06-09T12:00:00Z"
    )
    String createdAt;

    @Schema(
            description = "Thời điểm bài học được cập nhật gần đây nhất. Định dạng theo chuẩn ISO 8601.",
            type = "string",
            format = "date-time",
            example = "2025-06-09T12:30:00Z"
    )
    String updatedAt;

    @Schema(
            description = "Thông tin chi tiết về chương mà bài học này thuộc về.",
            implementation = ChapterResponse.class // Trả về toàn bộ ChapterResponse object
    )
    ChapterResponse chapter;
}
