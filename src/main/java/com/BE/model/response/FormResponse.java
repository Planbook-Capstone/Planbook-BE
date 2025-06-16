package com.BE.model.response;

import com.BE.enums.FormStatusEnum;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FormResponse {
    long id;
    String name;
    String description;
    FormStatusEnum status;
    JsonNode formData;
    String createdAt;
    String updatedAt;
}
