package com.BE.model.request;

import com.BE.model.dto.DifficultyCountDTO;
import com.BE.model.dto.IndividualBankExamDTO;
import com.BE.model.dto.SystemBankQuestionDTO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamGenerationRequest {

    @Schema(description = "Danh sách đề từ kho cá nhân (contentJson)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<IndividualBankExamDTO> personalExams;

    @Schema(description = "Danh sách câu hỏi rời từ kho hệ thống", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<SystemBankQuestionDTO> systemQuestions;

    @Schema(description = "Ma trận sinh đề theo phần và độ khó", required = true, example = "{ \"PHẦN I\": { \"NB\": 2, \"TH\": 2, \"VD\": 1 } }")
    private Map<String, DifficultyCountDTO> matrixConfig;

    @Schema(description = "Số đề muốn sinh ra", example = "2", required = true)
    private int numberOfExams;
}
