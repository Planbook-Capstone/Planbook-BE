package com.BE.model.response;

import com.BE.enums.ToolResultSource;
import com.BE.enums.ToolResultStatus;
import com.BE.enums.ToolResultType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO cho response của ToolResult
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToolResultResponse {

    private Long id;

    private UUID userId;

    private Long workspaceId;

    private ToolResultType type;

    private ToolResultSource source;

    private Long templateId;

    private List<Long> lessonIds;

    private String name;

    private String description;

    private Object data;

    private ToolResultStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
