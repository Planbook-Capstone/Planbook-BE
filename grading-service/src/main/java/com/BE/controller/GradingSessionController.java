package com.BE.controller;

import com.BE.model.request.GradingSessionRequest;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.GradingSessionResponse;
import com.BE.service.interfaceServices.GradingSessionService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/grading-sessions")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@Tag(name = "Phiên chấm điểm", description = "Các API để quản lý các phiên chấm điểm")
public class GradingSessionController {

    private final GradingSessionService gradingSessionService;
    private final ResponseHandler responseHandler;

    @Operation(summary = "Tạo một phiên chấm điểm mới",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dữ liệu để tạo một phiên chấm điểm mới.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GradingSessionRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Ví dụ tạo phiên chấm điểm",
                                            value = """
                                                    {
                                                      "name": "Hoá HK1 - 12A1",
                                                      "book_type_id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
                                                      "omr_template_id": 1,
                                                      "section_config_json": [
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
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    )
    @PostMapping
    public ResponseEntity<DataResponseDTO<GradingSessionResponse>> create(
            @Valid @org.springframework.web.bind.annotation.RequestBody GradingSessionRequest request) {
        GradingSessionResponse newSession = gradingSessionService.create(request);
        return responseHandler.response(HttpStatus.CREATED.value(), "Tạo phiên chấm điểm thành công", newSession);
    }


    @Operation(summary = "Lấy tất cả các phiên chấm điểm", description = "Lấy danh sách tất cả các phiên chấm điểm, có thể lọc theo bookTypeId.")
    @GetMapping
    public ResponseEntity<DataResponseDTO<List<GradingSessionResponse>>> getAll(@RequestParam(required = false) UUID bookTypeId) {
        List<GradingSessionResponse> sessions = gradingSessionService.getAll(bookTypeId);
        return responseHandler.response(HttpStatus.OK.value(), "Lấy danh sách phiên chấm điểm thành công", sessions);
    }
}

