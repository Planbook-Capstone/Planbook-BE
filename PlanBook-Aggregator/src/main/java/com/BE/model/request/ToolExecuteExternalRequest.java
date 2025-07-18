package com.BE.model.request;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ToolExecuteExternalRequest {

    String toolName;


    String tokenUrl;


    String apiUrl;


    String clientId;


    String clientSecret;


    Map<String, Object> payload;
}
