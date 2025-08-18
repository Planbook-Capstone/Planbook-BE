package com.BE.model.response;


import com.BE.enums.GenderEnum;
import com.BE.enums.RoleEnum;
import com.BE.enums.StatusEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserResponse {
    UUID id;
    String fullName;
    String username;
    String email;
    RoleEnum role;
    String phone;
    String avatar;
//    GenderEnum gender;
//    LocalDate birthday;
//    StatusEnum status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
//    WalletResponse wallet;
}
