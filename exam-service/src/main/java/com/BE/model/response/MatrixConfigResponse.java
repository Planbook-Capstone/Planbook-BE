package com.BE.model.response;

import com.BE.enums.StatusEnum;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatrixConfigResponse {
    Long id;
    String name;
    String description;
    JsonNode matrixJson;
    StatusEnum status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
