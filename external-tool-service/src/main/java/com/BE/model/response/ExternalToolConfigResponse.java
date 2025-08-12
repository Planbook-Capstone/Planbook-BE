package com.BE.model.response;

import com.BE.enums.StatusEnum;
import com.BE.enums.ToolCodeEnum;
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
    String apiUrl;
    String tokenUrl;
    String clientId;
    String clientSecret;
    String icon;
    ToolCodeEnum code;
    String href;
    Integer tokenCostPerQuery;
    ToolTypeEnum toolType;
    Map<String, Object> inputJson;
    String description;
    StatusEnum status;
    UUID createdBy;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
