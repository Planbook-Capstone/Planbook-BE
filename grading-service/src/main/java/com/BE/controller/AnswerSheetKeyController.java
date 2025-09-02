package com.BE.controller;

import com.BE.model.request.AnswerSheetKeyRequest;
import com.BE.model.request.AnswerSheetKeyUpdateRequest;
import com.BE.model.request.GradingSessionUpdateRequest;
import com.BE.model.response.AnswerSheetKeyResponse;
import com.BE.model.response.DataResponseDTO;
import com.BE.service.interfaceServices.AnswerSheetKeyService;
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

@RestController
@RequestMapping("/api/answer-sheet-keys")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@Tag(name = "Mã đề và Đáp án", description = "Các API để quản lý mã đề và đáp án")
public class AnswerSheetKeyController {

    private final AnswerSheetKeyService answerSheetKeyService;
    private final ResponseHandler responseHandler;

    @Operation(
            summary = "Tải lên một mã đề mới cùng với đáp án JSON",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dữ liệu để tải lên một mã đề mới.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AnswerSheetKeyRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Ví dụ mã đề đầy đủ",
                                            value = """
                                                    {
                                                      "grading_session_id": 1,
                                                      "code": "123",
                                                      "answer_json": [
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
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    )
    @PostMapping
    public ResponseEntity<DataResponseDTO<AnswerSheetKeyResponse>> create(
            @Valid @RequestBody AnswerSheetKeyRequest request) {
        AnswerSheetKeyResponse newKey = answerSheetKeyService.create(request);
        return responseHandler.response(HttpStatus.CREATED.value(), "Tạo mã đề thành công", newKey);
    }


    @Operation(summary = "Lấy tất cả các mã đề cho một phiên chấm điểm cụ thể")
    @GetMapping
    public ResponseEntity<DataResponseDTO<List<AnswerSheetKeyResponse>>> getByGradingSessionId(@RequestParam Long gradingSessionId) {
        List<AnswerSheetKeyResponse> keys = answerSheetKeyService.getByGradingSessionId(gradingSessionId);
        return responseHandler.response(HttpStatus.OK.value(), "Lấy danh sách mã đề thành công", keys);
    }


    @Operation(summary = "Cập nhật thông tin đáp án đúng")
    @PutMapping("/{id}")
    public ResponseEntity update(
            @PathVariable Long id,
            @Valid @RequestBody AnswerSheetKeyUpdateRequest request) {
        return responseHandler.response(HttpStatus.OK.value(), "Cập nhật đáp án đúng thành công", answerSheetKeyService.update(id, request));
    }

    @Operation(summary = "Xoá thông tin đáp án đúng")
    @DeleteMapping("/{id}")
    public ResponseEntity delete(
            @PathVariable Long id) {
        return responseHandler.response(HttpStatus.OK.value(), "Xoá đáp án đúng thành công", answerSheetKeyService.delete(id));
    }
}

