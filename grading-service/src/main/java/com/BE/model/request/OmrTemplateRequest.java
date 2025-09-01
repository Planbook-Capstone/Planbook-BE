package com.BE.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OmrTemplateRequest {

    @NotBlank(message = "Tên mẫu không được để trống")
    private String name;

    @JsonProperty("sample_image_url")
    private String sampleImageUrl;

}
