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
            description = "ID duy nhất của công cụ hỗ trợ. Giá trị này được hệ thống tự động sinh dưới dạng UUID.", // ĐÃ THAY ĐỔI MÔ TẢ
            type = "string",
            format = "uuid",
            example = "a1b2c3d4-e5f6-7890-1234-567890abcdef"
    )
    UUID id;

    @Schema(
            description = "Tên của công cụ hỗ trợ.", // ĐÃ THAY ĐỔI MÔ TẢ
            type = "string",
            example = "Chấm điểm" // ĐÃ THAY ĐỔI VÍ DỤ
    )
    String name;

    @Schema(
            description = "Mô tả chi tiết về công cụ hỗ trợ.", // ĐÃ THAY ĐỔI MÔ TẢ
            type = "string",
            example = "Công cụ giúp giảng viên quản lý và chấm điểm bài tập, bài kiểm tra của sinh viên." // ĐÃ THAY ĐỔI VÍ DỤ
    )
    String description;

    @Schema(
            description = "Icon đại diện cho công cụ hỗ trợ, thường là base64 encoded string của hình ảnh.", // ĐÃ THAY ĐỔI MÔ TẢ
            type = "string",
            example = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg=="
    )
    String icon;

    // THÊM TRƯỜNG MỚI NÀY
    @Schema(
            description = "Số lượng token bị trừ cho mỗi lượt hỏi khi sử dụng công cụ này.",
            example = "10",
            type = "integer",
            format = "int32"
    )
    Integer tokenCostPerQuery;

    @Schema(
            description = "Trạng thái hoạt động của công cụ hỗ trợ. Có thể là 'ACTIVE' hoặc 'INACTIVE'.", // ĐÃ THAY ĐỔI MÔ TẢ
            type = "string",
            allowableValues = { "ACTIVE", "INACTIVE" },
            example = "ACTIVE"
    )
    StatusEnum status;

    @Schema(
            description = "Thời điểm công cụ hỗ trợ được tạo lần đầu. Định dạng theo chuẩn ISO 8601.", // ĐÃ THAY ĐỔI MÔ TẢ
            type = "string",
            format = "date-time",
            example = "2025-06-11T10:30:00Z"
    )
    String createdAt;

    @Schema(
            description = "Thời điểm công cụ hỗ trợ được cập nhật gần đây nhất. Định dạng theo chuẩn ISO 8601.", // ĐÃ THAY ĐỔI MÔ TẢ
            type = "string",
            format = "date-time",
            example = "2025-06-11T11:00:00Z"
    )
    String updatedAt;

}