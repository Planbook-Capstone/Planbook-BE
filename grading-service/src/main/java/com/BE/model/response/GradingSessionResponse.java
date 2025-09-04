package com.BE.model.response;

import com.BE.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class GradingSessionResponse {

    private Long id;

    private String name;

    @JsonProperty("book_type_id")
    private UUID bookTypeId;

    private UUID userId;

    private StatusEnum status;

    @JsonProperty("omr_template")
    private com.BE.model.response.OmrTemplateResponse omrTemplate;

    private JsonNode sectionConfigJson;

    @JsonProperty("answer_sheet_keys")
    private List<com.BE.model.response.AnswerSheetKeyResponse> answerSheetKeys;

    @JsonProperty("total_submissions")
    private Integer totalSubmissions;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
