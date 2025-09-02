package com.BE.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentSubmissionRequest {

    @NotNull(message = "ID phiên chấm không được để trống")
    @JsonProperty("grading_session_id")
    private Long gradingSessionId;

    @NotBlank(message = "Mã học sinh không được để trống")
    @JsonProperty("student_code")
    private String studentCode;

    @NotBlank(message = "Mã đề nhận diện không được để trống")
    @JsonProperty("exam_code")
    private String examCode;

    @JsonProperty("image_base64")
    @NotBlank(message = "Ảnh (base64) không được để trống")
    private String imageBase64;

    @NotNull(message = "Dữ liệu JSON bài làm của học sinh không được để trống")
    @JsonProperty("student_answer_json")
    private JsonNode studentAnswerJson;
}
