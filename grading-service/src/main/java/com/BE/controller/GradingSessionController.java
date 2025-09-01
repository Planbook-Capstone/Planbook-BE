package com.BE.controller;

import com.BE.model.request.GradingSessionRequest;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.GradingSessionResponse;
import com.BE.service.interfaceServices.GradingSessionService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/grading-sessions")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@Tag(name = "Grading Session", description = "APIs for managing grading sessions")
public class GradingSessionController {

    private final GradingSessionService gradingSessionService;
    private final ResponseHandler responseHandler;

    @Operation(summary = "Create a new grading session")
    @PostMapping
    public ResponseEntity<DataResponseDTO<GradingSessionResponse>> create(@Valid @RequestBody GradingSessionRequest request) {
        GradingSessionResponse newSession = gradingSessionService.create(request);
        return responseHandler.response(HttpStatus.CREATED.value(), "Tạo phiên chấm điểm thành công", newSession);
    }

    @Operation(summary = "Get all grading sessions", description = "Get a list of all grading sessions, optionally filtered by bookTypeId.")
    @GetMapping
    public ResponseEntity<DataResponseDTO<List<GradingSessionResponse>>> getAll(@RequestParam(required = false) UUID bookTypeId) {
        List<GradingSessionResponse> sessions = gradingSessionService.getAll(bookTypeId);
        return responseHandler.response(HttpStatus.OK.value(), "Lấy danh sách phiên chấm điểm thành công", sessions);
    }
}

