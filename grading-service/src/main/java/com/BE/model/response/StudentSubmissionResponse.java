package com.BE.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentSubmissionResponse {

    private Long id;

    @JsonProperty("grading_session_id")
    private Long gradingSessionId;

    @JsonProperty("answer_sheet_key_id")
    private Long answerSheetKeyId;

    @JsonProperty("student_code")
    private String studentCode;

    @JsonProperty("exam_code")
    private String examCode;

    @JsonProperty("image_base64")
    private String imageBase64;

    private Float score;

    @JsonProperty("total_correct")
    private Integer totalCorrect;

    @JsonProperty("student_answer_json")
    private JsonNode studentAnswerJson;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
