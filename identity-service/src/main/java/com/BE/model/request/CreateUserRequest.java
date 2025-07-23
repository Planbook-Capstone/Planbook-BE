package com.BE.model.request;

import com.BE.enums.RoleEnum;
import com.BE.enums.StatusEnum;
import com.BE.exception.EnumValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên không được vượt quá 100 ký tự")
    @Schema(example = "Nguyễn Văn A")
    String fullName;

    @Email(message = "Email không hợp lệ", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotBlank(message = "Email không được để trống")
    String email;

    @Size(min = 5, message = "Tên người dùng phải có ít nhất 5 ký tự")
    @NotBlank(message = "Tên người dùng không được để trống")
    String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 5, message = "Mật khẩu phải có ít nhất 5 ký tự")
    String password;

    @Schema(example = "TEACHER, STAFF, PARTNER", description = "Role Enum")
    @EnumValidator(enumClass = RoleEnum.class, message = "Giá trị vai trò không hợp lệ")
    @Enumerated(EnumType.STRING)
    RoleEnum role;
}
