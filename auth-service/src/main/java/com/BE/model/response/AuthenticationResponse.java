package com.BE.model.response;


import com.BE.enums.GenderEnum;
import com.BE.enums.RoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AuthenticationResponse {
     UUID id;
     String fullName;
     String username;
     String email;
     RoleEnum role;
     String phone;
     String avatar;
     GenderEnum gender;
     LocalDate birthday;
     LocalDateTime createdAt;
     LocalDateTime updatedAt;
     String token;
     String refreshToken;
}

