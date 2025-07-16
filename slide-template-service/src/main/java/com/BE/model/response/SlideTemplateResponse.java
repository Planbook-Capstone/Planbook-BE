package com.BE.model.response;

import com.BE.enums.StatusEnum;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SlideTemplateResponse {
    long id;
    String name;
    String description;
    StatusEnum status;
    Map<String, Object> textBlocks;
    Map<String, Object> imageBlocks;
    String createdAt;
    String updatedAt;
}
