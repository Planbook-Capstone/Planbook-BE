package com.BE.model.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaData {
    private Long tool_log_id;
    private String user_id;
    private String lesson_id;
    private Object input;
    private String timestamp;
}