package com.BE.model.request;

import com.BE.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OmrTemplateFilterRequest {

    @Schema(description = "Tìm kiếm theo tên mẫu OMR (partial match)", example = "OMR A1")
    private String name;

    @Schema(description = "Trạng thái mẫu OMR: ACTIVE hoặc INACTIVE", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
    private StatusEnum status;

    @Schema(description = "Sắp xếp theo: name hoặc createdAt", example = "createdAt")
    private String sortBy = "createdAt";

    @Schema(description = "Thứ tự sắp xếp: asc hoặc desc", example = "desc", allowableValues = {"asc", "desc"})
    private String sortDirection = "desc";

    @Schema(description = "Số trang (bắt đầu từ 1)", example = "1")
    private int page = 1;

    @Schema(description = "Kích thước trang", example = "10")
    private int size = 10;
}

