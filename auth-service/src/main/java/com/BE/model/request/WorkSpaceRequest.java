package com.BE.model.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkSpaceRequest {
    @NotBlank(message = "name cannot be blank")
    private String name;
    @NotBlank(message = "academicYearId cannot be blank")
    private UUID academicYearId;
    @NotBlank(message = "userId cannot be blank")
    private UUID userId;
    // Getters and setters
}