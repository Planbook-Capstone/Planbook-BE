package com.BE.model.request;

import com.BE.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new lesson plan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request tạo giáo án mới")
public class CreateLessonPlanRequest {

    @NotBlank(message = "Tên giáo án không được để trống")
    @Schema(description = "Tên giáo án", example = "Bài 1: Hàm số bậc nhất", required = true)
    private String name;

    @Schema(description = "Mô tả giáo án", example = "Giáo án về hàm số bậc nhất cho lớp 10")
    private String description;

    @Schema(description = "Trạng thái", example = "ACTIVE")
    private Status status;
}
