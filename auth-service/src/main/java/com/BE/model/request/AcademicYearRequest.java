package com.BE.model.request;

import com.BE.enums.AcademicYearStatusEnum;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcademicYearRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;

}