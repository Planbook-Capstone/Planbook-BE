package com.BE.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndividualBankExamDTO {

    @Schema(description = "ID đề cá nhân", example = "123")
    private Long sourceExamId;

    @Schema(description = "Nội dung JSON của đề gồm parts/questions", required = true)
    private Map<String, Object> contentJson;
}
