package com.BE.model.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class EmailDataRequest {

    @Email(message = "Địa chỉ email không hợp lệ")
    @NotBlank(message = "ToEmail data không được để trống")
    private String toEmail;

    @NotBlank(message = "TemplateId data không được để trống")
    private String templateId;

    private Map<String, String> dynamicData;
}
