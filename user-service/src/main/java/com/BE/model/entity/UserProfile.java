package com.BE.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    UUID id; // Trùng khớp với AuthUser.id

    String fullName;

    String phone;

    String avatar;

    LocalDate birthday;

    String gender;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;
}
