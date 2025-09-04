package com.BE.controller;

import com.BE.model.request.StudentSubmissionRequest;
import com.BE.model.response.StudentSubmissionResponse;
import com.BE.service.interfaceServices.StudentSubmissionService;
import com.BE.model.response.DataResponseDTO;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student-submissions")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@Tag(name = "Bài làm của học sinh", description = "Các API để nộp và chấm điểm bài làm của học sinh")
public class StudentSubmissionController {

    private final StudentSubmissionService studentSubmissionService;
    private final ResponseHandler responseHandler;

    @Operation(
            summary = "Nộp và chấm điểm bài làm của học sinh",
            description = "Nhận bài làm của học sinh, so sánh với đáp án, chấm điểm và trả về kết quả chi tiết.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Chấm bài thành công",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponseDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Dữ liệu yêu cầu không hợp lệ"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy phiên chấm điểm hoặc mã đề")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dữ liệu bài làm của học sinh để nộp.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudentSubmissionRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Ví dụ bài làm đầy đủ",
                                            value = """
                                                    {
                                                      "grading_session_id": 1,
                                                      "student_code": "HS123456",
                                                      "exam_code": "M123",
                                                      "image_base64": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAABo4AAARVC...",
                                                      "total_correct": 10,
                                                      "score": 6,                                                                                  
                                                      "student_answer_json": [
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
    public ResponseEntity<DataResponseDTO<StudentSubmissionResponse>> submitAndGrade(
            @Valid @RequestBody StudentSubmissionRequest request) {
        StudentSubmissionResponse response = studentSubmissionService.createAndGradeSubmission(request);
        return responseHandler.response(HttpStatus.OK.value(), "Chấm bài thành công", response);
    }


    @Operation(summary = "Lấy bài làm của học sinh bằng ID")
    @GetMapping("/{id}")
    public ResponseEntity<DataResponseDTO<StudentSubmissionResponse>> getById(@PathVariable Long id) {
        StudentSubmissionResponse response = studentSubmissionService.getById(id);
        return responseHandler.response(HttpStatus.OK.value(), "Lấy bài làm thành công", response);
    }

    @Operation(summary = "Lấy tất cả bài làm của học sinh cho một phiên chấm điểm")
    @GetMapping
    public ResponseEntity<DataResponseDTO<java.util.List<StudentSubmissionResponse>>> getByGradingSessionId(@RequestParam Long gradingSessionId) {
        java.util.List<StudentSubmissionResponse> responses = studentSubmissionService.getByGradingSessionId(gradingSessionId);
        return responseHandler.response(HttpStatus.OK.value(), "Lấy danh sách bài làm thành công", responses);
    }
}

