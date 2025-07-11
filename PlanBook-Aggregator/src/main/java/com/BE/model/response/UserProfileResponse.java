package com.BE.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Dữ liệu trả về thông tin hồ sơ người dùng")
public class UserProfileResponse {

    @Schema(description = "ID người dùng", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    UUID id;

    @Schema(example = "Nguyễn Văn A")
    String fullName;

    @Schema(example = "+84123456789")
    String phone;

    @Schema(example = "https://example.com/avatar.jpg")
    String avatar;

    @Schema(example = "2000-01-01")
    LocalDate birthday;

    @Schema(example = "Nam")
    String gender;

    @Schema(description = "Ngày tạo hồ sơ")
    LocalDateTime createdAt;

    @Schema(description = "Ngày cập nhật gần nhất")
    LocalDateTime updatedAt;
}
