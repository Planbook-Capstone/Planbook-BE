package com.BE.controller;

import com.BE.model.request.ToolExecutionLogRequest;
import com.BE.model.request.ToolExecutionLogSearchRequest;
import com.BE.model.response.ToolExecutionLogResponse;
import com.BE.service.interfaceServices.IToolExecutionLogService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tool-logs")
@SecurityRequirement(name = "api")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ToolExecutionLogController {

    IToolExecutionLogService service;
    ResponseHandler responseHandler;


    @PostMapping
    @Operation(
            summary = "Ghi log việc thực thi công cụ",
            description = "API dùng để ghi lại thông tin input/output của các lần gọi công cụ từ aggregator hoặc frontend."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ghi log thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ToolExecutionLogResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity save(@Valid @RequestBody ToolExecutionLogRequest request) {
        return responseHandler.response(200, "Ghi log thành công", service.save(request));
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách log sử dụng tool",
            description = "Tìm kiếm theo input/output, lọc theo loại tool, người dùng, phân trang và sắp xếp.")
    @ApiResponse(responseCode = "200", description = "Thành công",
            content = @Content(mediaType = "application/json"))
    public ResponseEntity<?> getAll(@ParameterObject ToolExecutionLogSearchRequest request) {
        return responseHandler.response(200, "Lấy danh sách log thành công", service.getAll(request));
    }
}
