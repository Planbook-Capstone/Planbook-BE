package com.BE.model.response;

import com.BE.model.entity.AnswerSheetKey;
import com.BE.model.entity.GradingSession;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentSubmissionResponse {

    private Long id;

    private GradingSessionResponse gradingSession;

    private AnswerSheetKeyResponse answerSheetKey;

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
