package com.BE.model.response;

import com.BE.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OmrTemplateResponse {

    private Long id;

    private String name;

    @JsonProperty("sample_image_url")
    private String sampleImageUrl;

    private StatusEnum status;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
