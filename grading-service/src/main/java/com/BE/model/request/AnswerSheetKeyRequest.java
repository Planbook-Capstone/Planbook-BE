package com.BE.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnswerSheetKeyRequest {

    @NotBlank(message = "Mã đề không được để trống")
    private String code;

    @JsonProperty("image_url")
    private String imageUrl;

    @NotNull(message = "ID phiên chấm không được để trống")
    @JsonProperty("grading_session_id")
    private Long gradingSessionId;

    @NotNull(message = "Dữ liệu JSON đáp án không được để trống")
    @JsonProperty("answer_json")
    private JsonNode answerJson;
}
