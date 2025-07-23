package com.BE.model.response;

import com.BE.enums.AcademicYearStatusEnum;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcademicYearResponse {
    private UUID id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String yearLabel;
    private AcademicYearStatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Getters and setters
}