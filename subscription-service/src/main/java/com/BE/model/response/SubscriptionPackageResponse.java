package com.BE.model.response;

import com.BE.enums.StatusEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionPackageResponse {
    UUID id;
    String name;
    Integer tokenAmount;
    BigDecimal price;
    String description;
    StatusEnum status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
