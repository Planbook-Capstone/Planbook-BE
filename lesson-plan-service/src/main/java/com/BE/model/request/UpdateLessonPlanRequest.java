package com.BE.model.request;

import com.BE.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating a lesson plan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request cập nhật giáo án")
public class UpdateLessonPlanRequest {

    @Schema(description = "Tên giáo án", example = "Bài 1: Hàm số bậc nhất")
    private String name;

    @Schema(description = "Mô tả giáo án", example = "Giáo án về hàm số bậc nhất cho lớp 10")
    private String description;
}
