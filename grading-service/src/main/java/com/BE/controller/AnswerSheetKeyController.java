package com.BE.controller;

import com.BE.model.request.AnswerSheetKeyRequest;
import com.BE.model.response.AnswerSheetKeyResponse;
import com.BE.model.response.DataResponseDTO;
import com.BE.service.interfaceServices.AnswerSheetKeyService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Answer Sheet Key", description = "APIs for managing answer sheet keys")
public class AnswerSheetKeyController {

    private final AnswerSheetKeyService answerSheetKeyService;
    private final ResponseHandler responseHandler;

    @Operation(summary = "Upload a new answer sheet key with its JSON answers")
    @PostMapping
    public ResponseEntity<DataResponseDTO<AnswerSheetKeyResponse>> create(@Valid @RequestBody AnswerSheetKeyRequest request) {
        AnswerSheetKeyResponse newKey = answerSheetKeyService.create(request);
        return responseHandler.response(HttpStatus.CREATED.value(), "Tạo mã đề thành công", newKey);
    }

    @Operation(summary = "Get all answer sheet keys for a specific grading session")
    @GetMapping
    public ResponseEntity<DataResponseDTO<List<AnswerSheetKeyResponse>>> getByGradingSessionId(@RequestParam Long gradingSessionId) {
        List<AnswerSheetKeyResponse> keys = answerSheetKeyService.getByGradingSessionId(gradingSessionId);
        return responseHandler.response(HttpStatus.OK.value(), "Lấy danh sách mã đề thành công", keys);
    }
}

