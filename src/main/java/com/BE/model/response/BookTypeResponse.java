package com.BE.model.response;

import com.BE.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID; // Quan trọng: Sử dụng UUID cho ID

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Response DTO chứa thông tin chi tiết của Loại Sách")
public class BookTypeResponse {

    @Schema(
            description = "ID duy nhất của loại sách. Giá trị này được hệ thống tự động sinh dưới dạng UUID.",
            type = "string", // Kiểu string cho UUID trong JSON
            format = "uuid",
            example = "a1b2c3d4-e5f6-7890-1234-567890abcdef"
    )
    UUID id; // Quan trọng: Kiểu UUID

    @Schema(
            description = "Tên của loại sách.",
            type = "string",
            example = "Sách giáo khoa"
    )
    String name;

    @Schema(
            description = "Mô tả chi tiết về loại sách.",
            type = "string",
            example = "Loại sách dùng cho việc học tập chính khóa trên lớp."
    )
    String description;

    @Schema(
            description = "Icon đại diện cho loại sách, thường là base64 encoded string của hình ảnh.",
            type = "string",
            example = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg=="
    )
    String icon;

    @Schema(
            description = "Trạng thái hoạt động của loại sách. Có thể là 'ACTIVE' hoặc 'INACTIVE'.",
            type = "string",
            allowableValues = { "ACTIVE", "INACTIVE" },
            example = "ACTIVE"
    )
    StatusEnum status;

    @Schema(
            description = "Thời điểm loại sách được tạo lần đầu. Định dạng theo chuẩn ISO 8601.",
            type = "string",
            format = "date-time",
            example = "2025-06-11T10:30:00Z"
    )
    String createdAt;

    @Schema(
            description = "Thời điểm loại sách được cập nhật gần đây nhất. Định dạng theo chuẩn ISO 8601.",
            type = "string",
            format = "date-time",
            example = "2025-06-11T11:00:00Z"
    )
    String updatedAt;
}