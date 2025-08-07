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

    @Schema(description = "Tên trường tổ chức kiểm tra", example = "THPT Lê Quý Đôn")
    private String school;

    @Schema(description = "Tiêu đề bài kiểm tra hoặc kỳ thi", example = "Đề thi học kỳ 1 môn Hóa học lớp 10")
    private String examTitle;

    @Schema(description = "Thời gian làm bài (phút)", example = "45")
    private int duration;

    @Schema(description = "Danh sách đề từ kho cá nhân (contentJson)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<IndividualBankExamDTO> personalExams;

    @Schema(description = "Danh sách câu hỏi rời từ kho hệ thống", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<SystemBankQuestionDTO> systemQuestions;

    @Schema(description = "Ma trận sinh đề theo phần và độ khó", required = true, example = "{ \"PHẦN I\": { \"nb\": 2, \"th\": 2, \"vd\": 1 } }")
    private Map<String, DifficultyCountDTO> matrixConfig;

    @Schema(description = "Số đề muốn sinh ra", example = "2", required = true)
    private int numberOfExams;
}
