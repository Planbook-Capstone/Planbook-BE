package com.BE.model.response;

import com.BE.enums.StatusEnum;
import com.BE.enums.ToolStatusEnum;
import com.BE.enums.ToolTypeEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExternalToolConfigResponse {
    UUID id;
    String name;
    String icon;
    String apiUrl;
    String tokenUrl;
    String clientId;
    String clientSecret;
    Integer tokenCostPerQuery;
    ToolTypeEnum toolType;
    Map<String, Object> inputJson;
    String description;
    ToolStatusEnum status;
    UUID createdBy;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
