package com.BE.model.response;

import com.BE.enums.StatusEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExternalToolConfigResponse {
    Long id;
    String name;
    String apiUrl;
    String tokenUrl;
    String clientId;
    String description;
    StatusEnum status;
    UUID createdBy;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
