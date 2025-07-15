package com.BE.model.request;


import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class WebSocketMessageRequest {
    private String userId;        // username principal - thường là userId hoặc email
    private String destination;   // ví dụ: "/queue/notifications" hoặc "/topic/chat"
    private Map<String, Object> payload;       // nội dung bất kỳ (message, object,...)
}
