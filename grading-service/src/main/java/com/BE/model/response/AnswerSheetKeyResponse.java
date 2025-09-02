package com.BE.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnswerSheetKeyResponse {

    private Long id;

    private String code;

    @JsonProperty("grading_session_id")
    private Long gradingSessionId;

    @JsonProperty("answer_json")
    private JsonNode answerJson;

    @JsonProperty("total_submissions")
    private Integer totalSubmissions;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
