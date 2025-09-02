package com.BE.model.request;

import com.BE.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
public class GradingSessionFilterRequest {

    @Schema(description = "Tìm kiếm theo tên phiên chấm (partial match)", example = "Hoá HK1")
    private String name;

    @Schema(description = "Trạng thái mẫu OMR: ACTIVE hoặc INACTIVE", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
    private StatusEnum status;

    @Schema(description = "Lọc theo ID loại sách (book type)", example = "c1f4eea9-5a6e-4b4c-9c45-8478d7efce0a")
    private UUID bookTypeId;

    @Schema(description = "Sắp xếp theo: name hoặc createdAt", example = "createdAt")
    private String sortBy = "createdAt";

    @Schema(description = "Thứ tự sắp xếp: asc hoặc desc", example = "desc", allowableValues = {"asc", "desc"})
    private String sortDirection = "desc";

    @Schema(description = "Số trang (bắt đầu từ 1)", example = "1")
    private int page = 1;

    @Schema(description = "Kích thước trang", example = "10")
    private int size = 10;
}
