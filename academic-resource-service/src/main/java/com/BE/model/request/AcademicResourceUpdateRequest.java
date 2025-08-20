package com.BE.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcademicResourceUpdateRequest {

    @Schema(example = "image", description = "Loại tài nguyên: image, gif, video, webp, iframe")
    @Size(max = 20, message = "Loại tài nguyên không được vượt quá 20 ký tự")
    String type;

    @Schema(example = "Bộ sưu tập công thức Toán đã cập nhật", description = "Tên tài nguyên")
    @Size(max = 255, message = "Tên tài nguyên không được vượt quá 255 ký tự")
    String name;

    @Schema(example = "Bộ sưu tập các công thức toán quan trọng đã cập nhật", description = "Mô tả tài nguyên")
    String description;

    @Schema(example = "https://example.com/updated-resource.pdf", description = "URL tài nguyên hoặc đường dẫn tệp")
    String url;

    @Schema(example = "[1, 2, 4]", description = "List of tag IDs to associate with this resource")
    Set<Long> tagIds;
}
