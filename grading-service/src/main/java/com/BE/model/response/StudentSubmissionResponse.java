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

    @JsonProperty("detected_code")
    private String detectedCode;

    @JsonProperty("image_url")
    private String imageUrl;

    private Float score;

    @JsonProperty("total_correct")
    private Integer totalCorrect;

    @JsonProperty("submitted_at")
    private LocalDateTime submittedAt;

    @JsonProperty("student_answer_json")
    private JsonNode studentAnswerJson;

    @JsonProperty("graded_answers")
    private JsonNode gradedAnswers;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
