package com.BE.model.response;

import com.BE.enums.SubscriptionStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    SubscriptionStatus status;
    boolean highlight;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Map<String, Object> features;
}
