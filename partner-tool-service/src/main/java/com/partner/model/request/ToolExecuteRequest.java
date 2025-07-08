package com.partner.model.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ToolExecuteRequest {

    String toolName;


    String tokenUrl;


    String apiUrl;


    String clientId;


    String clientSecret;


    Map<String, Object> payload;
}
