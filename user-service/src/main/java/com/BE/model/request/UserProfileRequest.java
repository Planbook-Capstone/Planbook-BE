package com.BE.model.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Dữ liệu yêu cầu cập nhật hồ sơ người dùng")
public class UserProfileRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên không vượt quá 100 ký tự")
    @Schema(example = "Nguyễn Văn A")
    String fullName;

    @Pattern(
            regexp = "^(\\+84|0)(3[2-9]|5[2-9]|7[0-9]|8[1-9]|9[0-9])[0-9]{7}$",
            message = "Số điện thoại không hợp lệ. Phải là số điện thoại Việt Nam đúng định dạng."
    )
    @Schema(example = "0987654321", description = "Số điện thoại hợp lệ của Việt Nam")
    String phone;


    @Schema(example = "https://example.com/avatar.jpg", description = "URL ảnh đại diện")
    String avatar;

    @Past(message = "Ngày sinh phải là trong quá khứ")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Schema(example = "01-01-2000", description = "Ngày sinh (định dạng dd-MM-yyyy)")
    LocalDate birthday;

    @Pattern(regexp = "^(Nam|Nữ|Khác)?$", message = "Giới tính phải là Nam, Nữ hoặc Khác")
    @Schema(example = "Nam", allowableValues = {"Nam", "Nữ", "Khác"})
    String gender;


}
