package com.BE.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class GradingSessionRequest {

    @NotBlank(message = "Tên phiên chấm không được để trống")
    private String name;

    @NotNull(message = "ID loại sách không được để trống")
    @JsonProperty("book_type_id")
    private UUID bookTypeId;

    @NotNull(message = "ID mẫu OMR không được để trống")
    @JsonProperty("omr_template_id")
    private Long omrTemplateId;

    @JsonProperty("section_config_json")
    private JsonNode sectionConfigJson;
}
