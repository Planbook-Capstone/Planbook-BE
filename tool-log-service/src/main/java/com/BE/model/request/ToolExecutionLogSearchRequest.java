package com.BE.model.request;

import com.BE.enums.ToolTypeEnum;
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
public class ToolExecutionLogSearchRequest {

    @Schema(description = "Trang bắt đầu (>=1)", example = "1", defaultValue = "1")
    Integer offset = 1;

    @Schema(description = "Số lượng phần tử mỗi trang", example = "10", defaultValue = "10")
    Integer pageSize = 10;

    @Schema(description = "Trường sắp xếp", example = "createdAt", defaultValue = "createdAt")
    String sortBy = "createdAt";

    @Schema(description = "Thứ tự sắp xếp", allowableValues = {"asc", "desc"}, defaultValue = "desc")
    String sortDirection = "desc";

    @Schema(description = "Tìm kiếm theo input hoặc output")
    String search;

    @Schema(description = "Lọc theo tool type", allowableValues = {"INTERNAL", "EXTERNAL"}, example = "EXTERNAL")
    ToolTypeEnum toolType;

    @Schema(description = "Lọc theo người dùng tạo")
    UUID userId;
}
