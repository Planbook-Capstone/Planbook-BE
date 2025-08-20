package com.BE.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class AcademicResourceCreateRequest {

    @Schema(
            description = "ID của bài học",
            example = "1"
    )
    Long lessonId;

    @Schema(example = "image", description = "Loại tài nguyên: image, gif, video, webp, iframe")
    @NotBlank(message = "Loại tài nguyên là bắt buộc")
    @Size(max = 20, message = "Loại tài nguyên không được vượt quá 20 ký tự")
    String type;

    @Schema(example = "Bộ sưu tập công thức Toán", description = "Tên tài nguyên")
    @NotBlank(message = "Tên tài nguyên là bắt buộc")
    @Size(max = 255, message = "Tên tài nguyên không được vượt quá 255 ký tự")
    String name;

    @Schema(example = "Bộ sưu tập các công thức toán quan trọng cho lớp 10", description = "Mô tả tài nguyên")
    String description;

    @Schema(example = "https://example.com/resource.pdf", description = "URL tài nguyên hoặc đường dẫn tệp")
    String url; // Optional if file is being uploaded

    @Schema(example = "[1, 2, 3]", description = "Danh sách ID loại học liệu để liên kết với tài nguyên này")
    Set<Long> tagIds;


}
