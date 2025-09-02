package com.BE.model.request;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AnswerSheetKeyUpdateRequest {
    @Schema(description = "Mã đề", example = "123")
    private String code;

    @Schema(description = "Cấu hình đáp án đúng", example = """
                                                      [
                                                        {
                                                          "sectionOrder": 1,
                                                          "sectionType": "MULTIPLE_CHOICE",
                                                          "questions": [
                                                            { "questionNumber": 1, "answer": "A" },
                                                            { "questionNumber": 2, "answer": "C" }
                                                          ]
                                                        },
                                                        {
                                                          "sectionOrder": 2,
                                                          "sectionType": "TRUE_FALSE",
                                                          "questions": [
                                                            {
                                                              "questionNumber": 1,
                                                              "answer": {
                                                                "a": "Đ",
                                                                "b": "S",
                                                                "c": "Đ",
                                                                "d": "S"
                                                              }
                                                            }
                                                          ]
                                                        },
                                                        {
                                                          "sectionOrder": 3,
                                                          "sectionType": "ESSAY_CODE",
                                                          "questions": [
                                                            { "questionNumber": 1, "answer": "2810" }
                                                          ]
                                                        }
                                                      ]
            """)
    private JsonNode answerJson;
}
