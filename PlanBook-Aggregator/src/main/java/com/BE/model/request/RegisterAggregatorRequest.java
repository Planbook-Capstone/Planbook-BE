package com.BE.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterAggregatorRequest {

    @Email(message = "Email không hợp lệ", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+$")
    @NotBlank(message = "Email không được để trống")
    @Schema(example = "user@gmail.com")
    String email;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 5)
    @Schema(example = "user123")
    String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 5)
    @Schema(example = "secret123")
    String password;

    @NotBlank(message = "Họ tên không được để trống")
    @Schema(example = "Nguyễn Văn A")
    String fullName;

    @Pattern(regexp = "^(\\+84|0)(3[2-9]|5[2689]|7[06-9]|8[1-689]|9[0-9])[0-9]{7}$",
            message = "Số điện thoại không hợp lệ")
    @Schema(example = "0987654321")
    String phone;

    @Schema(example = "https://example.com/avatar.jpg")
    String avatar;

    @Past
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Schema(example = "01-01-2000")
    LocalDate birthday;

    @Pattern(regexp = "^(Nam|Nữ|Khác)?$")
    @Schema(example = "Nam", allowableValues = {"Nam", "Nữ", "Khác"})
    String gender;
}
