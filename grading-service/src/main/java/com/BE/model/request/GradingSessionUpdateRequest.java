package com.BE.model.request;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GradingSessionUpdateRequest {

    @Schema(description = "Tên phiên chấm", example = "Phiên chấm Hóa HK1 - 12A1")
    private String name;

    @Schema(description = "Cấu hình điểm từng phần", example = """
                                                      [
                                                        {
                                                          "sectionOrder": 1,
                                                          "sectionType": "MULTIPLE_CHOICE",
                                                          "questionCount": 40,
                                                          "pointsPerQuestion": 0.25
                                                        },
                                                        {
                                                          "sectionOrder": 2,
                                                          "sectionType": "TRUE_FALSE",
                                                          "questionCount": 4,
                                                          "rule": {
                                                            "1": 0.25,
                                                            "2": 0.5,
                                                            "3": 0.75,
                                                            "4": 1.0
                                                          }
                                                        },
                                                        {
                                                          "sectionOrder": 3,
                                                          "sectionType": "ESSAY",
                                                          "questionCount": 2,
                                                          "pointsPerQuestion": 1.5
                                                        }
                                                      ]
            """)
    private JsonNode sectionConfigJson;
}
