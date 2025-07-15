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
    private String user_id;
    private String lesson_id;
    private Object lesson_plan_json;  // chính là parsed inputJson
    private String timestamp;
}