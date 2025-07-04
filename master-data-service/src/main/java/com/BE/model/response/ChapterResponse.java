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
public class ChapterResponse {

    @Schema(
            description = "ID duy nhất của chương. Giá trị này được hệ thống tự động sinh.",
            type = "integer",
            format = "int64",
            example = "1"
    )
    long id;

    @Schema(
            description = "Tên của chương.",
            type = "string",
            example = "Chương 1: Đại cương về hóa học"
    )
    String name;

    @Schema(
            description = "Trạng thái hoạt động của chương. Có thể là 'ACTIVE' hoặc 'INACTIVE'.",
            type = "string",
            allowableValues = { "ACTIVE", "INACTIVE" },
            example = "ACTIVE"
    )
    StatusEnum status;

    @Schema(
            description = "Thời điểm chương được tạo lần đầu. Định dạng theo chuẩn ISO 8601.",
            type = "string",
            format = "date-time",
            example = "2025-06-09T11:00:00Z"
    )
    String createdAt;

    @Schema(
            description = "Thời điểm chương được cập nhật gần đây nhất. Định dạng theo chuẩn ISO 8601.",
            type = "string",
            format = "date-time",
            example = "2025-06-09T11:30:00Z"
    )
    String updatedAt;

    @Schema(
            description = "Thông tin chi tiết về cuốn sách mà chương này thuộc về.",
            implementation = BookResponse.class // Trả về toàn bộ BookResponse object
    )
    BookResponse book;
}
