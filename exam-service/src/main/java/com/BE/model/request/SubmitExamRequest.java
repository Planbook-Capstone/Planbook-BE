package com.BE.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to submit exam answers")
public class SubmitExamRequest {

    @Schema(description = "Name of the student submitting the exam",
        example = "Nguyen Van A", required = true)
    @NotBlank(message = "Tên học sinh là bắt buộc")
    private String studentName;

    @Schema(description = "Student's answers in simplified format", required = true)
    @NotNull(message = "Câu trả lời là bắt buộc")
    private List<Map<String, Object>> answers;
}
