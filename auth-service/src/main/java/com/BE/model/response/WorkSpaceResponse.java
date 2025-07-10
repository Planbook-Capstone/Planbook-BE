package com.BE.model.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkSpaceResponse {
    private UUID id;
    private String name;
    private UUID academicYearId;
    private UUID authId;
    private String createdAt;
    private String updatedAt;
    // Getters and setters
}