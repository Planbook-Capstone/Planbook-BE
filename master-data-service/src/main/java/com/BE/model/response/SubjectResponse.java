package com.BE.model.response;

import com.BE.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Response DTO chứa thông tin chi tiết của Môn học")
public class SubjectResponse {

    @Schema(
            description = "ID duy nhất của môn học. Giá trị này được hệ thống tự động sinh.",
            type = "integer",
            format = "int64",
            example = "1"
    )
    long id;

    @Schema(
            description = "Tên của môn học.",
            type = "string",
            example = "Toán học"
    )
    String name;

    @Schema(
            description = "Trạng thái hoạt động của môn học. Có thể là 'ACTIVE' (đang hoạt động) hoặc 'INACTIVE' (không hoạt động).",
            type = "string",
            allowableValues = { "ACTIVE", "INACTIVE" }, // Các giá trị hợp lệ từ StatusEnum
            example = "ACTIVE"
    )
    StatusEnum status; // Giữ nguyên kiểu Enum để ánh xạ dễ dàng

    @Schema(
            description = "Thời điểm môn học được tạo lần đầu. Định dạng theo chuẩn ISO 8601.",
            type = "string",
            format = "date-time",
            example = "2025-06-08T10:00:00Z"
    )
    String createdAt;

    @Schema(
            description = "Thời điểm môn học được cập nhật gần đây nhất. Định dạng theo chuẩn ISO 8601.",
            type = "string",
            format = "date-time",
            example = "2025-06-08T10:30:00Z"
    )
    String updatedAt;

//    @Schema(
//            description = "ID của khối lớp mà môn học này thuộc về.",
//            type = "integer",
//            format = "int64",
//            example = "1"
//    )
//    Long gradeId; // ID của Grade mà Subject này thuộc về

    @Schema(
            description = "Thông tin chi tiết về khối lớp mà môn học này thuộc về.",
            implementation = GradeResponse.class // THAY ĐỔI Ở ĐÂY: Trỏ đến GradeResponse class
    )
    GradeResponse grade;

// Tùy chọn: Bao gồm danh sách sách (BookResponse) nếu bạn muốn trả về kèm thông tin sách
// Nếu bạn muốn tránh vòng lặp hoặc thông tin quá lớn, có thể bỏ qua trường này
//    /*
//    @Schema(
//            description = "Danh sách các cuốn sách thuộc môn học này.",
//            type = "array",
//            implementation = BookResponse.class // Bạn cần tạo BookResponse DTO tương ứng
//    )
//    Set<BookResponse> books;
}
