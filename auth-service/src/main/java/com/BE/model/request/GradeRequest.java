package com.BE.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request DTO for creating/updating a Grade")
public class GradeRequest {

    @Schema(
            description = "Tên khối lớp",
            example = "Lớp 10",
            required = true,
            type = "string"
    )
    @NotBlank(message = "Grade name cannot be blank")
    private String name;

}
