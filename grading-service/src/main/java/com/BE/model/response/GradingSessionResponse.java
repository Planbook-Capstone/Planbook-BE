package com.BE.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class GradingSessionResponse {

    private Long id;

    private String name;

    @JsonProperty("book_type_id")
    private UUID bookTypeId;

    @JsonProperty("session_date")
    private LocalDate sessionDate;

        @JsonProperty("omr_template")
    private com.BE.model.response.OmrTemplateResponse omrTemplate;

    @JsonProperty("answer_sheet_keys")
    private List<com.BE.model.response.AnswerSheetKeyResponse> answerSheetKeys;

    @JsonProperty("total_submissions")
    private Integer totalSubmissions;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
