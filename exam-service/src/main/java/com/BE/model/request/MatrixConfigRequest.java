package com.BE.model.request;

import com.BE.enums.StatusEnum;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatrixConfigRequest {
        @NotBlank(message = "Tên không được để trống")
        String name;
        String description;
        @NotNull(message = "Dữ liệu ma trận không được để trống")
        JsonNode matrixJson;
}
