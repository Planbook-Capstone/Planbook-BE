package com.BE.model.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToolKafkaPayload {

    private String type; // = "lesson_plan_content_generation_request"

    private KafkaData data;

}