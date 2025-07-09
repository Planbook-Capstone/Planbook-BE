package com.BE.model.request;

import com.BE.enums.ExternalToolSortByEnum;
import com.BE.enums.SortDirectionEnum;
import com.BE.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExternalToolSearchRequest {

    @Schema(description = "Lọc theo người tạo (UUID)", example = "")
    UUID createdBy;

    @Schema(description = "Trang bắt đầu (>=1)", example = "1", defaultValue = "1")
    Integer offset;

    @Schema(description = "Số lượng phần tử mỗi trang", example = "10", defaultValue = "10")
    Integer pageSize;

    @Schema(
            description = "Trường để sắp xếp",
            example = "createdAt",
            defaultValue = "createdAt",
            allowableValues = {"createdAt", "name", "status"}
    )
    ExternalToolSortByEnum sortBy;

    @Schema(
            description = "Thứ tự sắp xếp: asc hoặc desc",
            example = "desc",
            defaultValue = "desc",
            allowableValues = {"asc", "desc"}
    )
    SortDirectionEnum sortDirection;

    @Schema(description = "Từ khóa tìm kiếm theo tên hoặc mô tả", example = "")
    String search;

    @Schema(
            description = "Trạng thái hiện tại của công cụ. " +
                    "Có thể là: PENDING (chờ duyệt), APPROVED (đã duyệt), " +
                    "ACTIVE (đang hoạt động), INACTIVE (ngừng hoạt động), " +
                    "REJECTED (bị từ chối), CANCELLED (bị hủy), DELETED (đã xóa).",
            implementation = StatusEnum.class,
            allowableValues = {
                    "PENDING", "APPROVED", "ACTIVE", "INACTIVE",
                    "REJECTED", "CANCELLED", "DELETED"
            }
    )
    StatusEnum status;

}

