package com.BE.model.response;

import com.BE.enums.StatusEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.fasterxml.jackson.databind.JsonNode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SlideDetailResponse {
    String id;
    String title;
    Object slideData; // JSON object của slide
    String description; // Mô tả placeholder
    StatusEnum status;
    Long slideTemplateId;
    String slideTemplateName;
    String createdAt;
    String updatedAt;
}
