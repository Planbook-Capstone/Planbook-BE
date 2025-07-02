package com.BE.model.dto;

import com.BE.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for LessonPlan entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin giáo án")
public class LessonPlanDTO {

    @Schema(description = "ID của giáo án", example = "1")
    private Long id;

    @Schema(description = "Tên giáo án", example = "Bài 1: Hàm số bậc nhất")
    private String name;

    @Schema(description = "Mô tả giáo án", example = "Giáo án về hàm số bậc nhất cho lớp 10")
    private String description;

    @Schema(description = "Thời gian tạo")
    private LocalDateTime createdAt;

    @Schema(description = "Thời gian cập nhật")
    private LocalDateTime updatedAt;

    @Schema(description = "Trạng thái", example = "ACTIVE")
    private Status status;
}
