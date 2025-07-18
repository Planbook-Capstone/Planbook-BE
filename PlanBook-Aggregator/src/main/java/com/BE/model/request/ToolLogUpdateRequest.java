package com.BE.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ToolLogUpdateRequest {

    @NotNull(message = "Trạng thái thành công không được để trống")
    @Schema(description = "true nếu tool thực thi thành công", example = "true")
    Boolean success;

    @Schema(description = "Kết quả trả về từ công cụ", example = "{\"result\": \"Bản dịch hoàn tất\"}")
    Map<String, Object> output;
}
