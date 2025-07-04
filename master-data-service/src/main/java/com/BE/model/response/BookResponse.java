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
public class BookResponse {

    @Schema(
            description = "ID duy nhất của cuốn sách. Giá trị này được hệ thống tự động sinh.",
            type = "integer",
            format = "int64",
            example = "1"
    )
    long id;

    @Schema(
            description = "Tên của cuốn sách.",
            type = "string",
            example = "Sách giáo khoa Toán 10"
    )
    String name;

    @Schema(
            description = "Trạng thái hoạt động của cuốn sách. Có thể là 'ACTIVE' hoặc 'INACTIVE'.",
            type = "string",
            allowableValues = { "ACTIVE", "INACTIVE" },
            example = "ACTIVE"
    )
    StatusEnum status;

    @Schema(
            description = "Thời điểm cuốn sách được tạo lần đầu. Định dạng theo chuẩn ISO 8601.",
            type = "string",
            format = "date-time",
            example = "2025-06-09T10:00:00Z"
    )
    String createdAt;

    @Schema(
            description = "Thời điểm cuốn sách được cập nhật gần đây nhất. Định dạng theo chuẩn ISO 8601.",
            type = "string",
            format = "date-time",
            example = "2025-06-09T10:30:00Z"
    )
    String updatedAt;

    @Schema(
            description = "Thông tin chi tiết về môn học mà cuốn sách này thuộc về.",
            implementation = SubjectResponse.class // Trả về toàn bộ SubjectResponse object
    )
    SubjectResponse subject;
}
