package com.BE.model.response;

import com.BE.enums.RoleEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {

    UUID id;
    String username;
    String email;
    RoleEnum role;
    String token;
    String refreshToken;
    String fullName;
}
