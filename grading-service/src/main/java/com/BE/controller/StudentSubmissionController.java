package com.BE.controller;

import com.BE.model.request.StudentSubmissionRequest;
import com.BE.model.response.StudentSubmissionResponse;
import com.BE.service.interfaceServices.StudentSubmissionService;
import com.BE.model.response.DataResponseDTO;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
@Tag(name = "Student Submission", description = "APIs for submitting and grading student answer sheets")
public class StudentSubmissionController {

    private final StudentSubmissionService studentSubmissionService;
        private final ResponseHandler responseHandler;

    @Operation(summary = "Submit and grade a student's answer sheet",
               description = "Receives a student's submission, compares it with the answer key, grades it, and returns the detailed result.",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Successfully graded submission",
                                content = @Content(mediaType = "application/json",
                                                   schema = @Schema(implementation = DataResponseDTO.class))),
                   @ApiResponse(responseCode = "400", description = "Invalid request data"),
                   @ApiResponse(responseCode = "404", description = "Grading session or answer key not found")
               })
    @PostMapping
    public ResponseEntity<DataResponseDTO<StudentSubmissionResponse>> submitAndGrade(@Valid @RequestBody StudentSubmissionRequest request) {
        StudentSubmissionResponse response = studentSubmissionService.createAndGradeSubmission(request);
        return responseHandler.response(HttpStatus.OK.value(), "Chấm bài thành công", response);
    }

    @Operation(summary = "Get a student submission by ID")
    @GetMapping("/{id}")
    public ResponseEntity<DataResponseDTO<StudentSubmissionResponse>> getById(@PathVariable Long id) {
        StudentSubmissionResponse response = studentSubmissionService.getById(id);
        return responseHandler.response(HttpStatus.OK.value(), "Lấy bài làm thành công", response);
    }

    @Operation(summary = "Get all student submissions for a grading session")
    @GetMapping
    public ResponseEntity<DataResponseDTO<java.util.List<StudentSubmissionResponse>>> getByGradingSessionId(@RequestParam Long gradingSessionId) {
        java.util.List<StudentSubmissionResponse> responses = studentSubmissionService.getByGradingSessionId(gradingSessionId);
        return responseHandler.response(HttpStatus.OK.value(), "Lấy danh sách bài làm thành công", responses);
    }
}

