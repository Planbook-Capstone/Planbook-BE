package com.BE.controller;


import com.BE.model.request.ExamGenerationRequest;
import com.BE.service.interfaceService.IExamGenerationService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exam-generator")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
public class ExamGenerationController {

    private final IExamGenerationService service;
    private final ResponseHandler responseHandler;

    @PostMapping
    @Operation(summary = "Sinh đề thi ngẫu nhiên từ nhiều đề mẫu", description = "Trả về danh sách đề thi mới dạng contentJson chưa lưu")
    @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(mediaType = "application/json"))
    public ResponseEntity<?> generateRandomExams(@RequestBody ExamGenerationRequest request) {
        List<Map<String, Object>> generatedExams = service.generateExams(request);
        return responseHandler.response(200, "Sinh đề thành công", generatedExams);
    }

    
}