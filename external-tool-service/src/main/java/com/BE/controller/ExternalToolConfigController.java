package com.BE.controller;

import com.BE.model.request.ExternalToolConfigRequest;
import com.BE.model.response.ExternalToolConfigResponse;
import com.BE.service.interfaceServices.IExternalToolConfigService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/external-tools")
@RequiredArgsConstructor
public class ExternalToolConfigController {

    private final IExternalToolConfigService service;
    private final ResponseHandler responseHandler;

    @PostMapping
    @Operation(summary = "Tạo cấu hình công cụ bên thứ ba",
            description = "API này cho phép bạn thêm cấu hình tích hợp các công cụ như hệ thống SMS, AI, hay dịch vụ REST khác.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo cấu hình thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExternalToolConfigResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Thông tin cấu hình của công cụ cần tích hợp",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ExternalToolConfigRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "Ví dụ cấu hình AI Tool",
                                    summary = "Công cụ AI nội bộ",
                                    value = """
                                    {
                                      "name": "AI-Writer",
                                      "apiUrl": "https://api.aiwriter.com/generate",
                                      "tokenUrl": "https://api.aiwriter.com/token",
                                      "clientId": "ai-client",
                                      "clientSecret": "super-secret",
                                      "description": "Tích hợp AI để tạo văn bản",
                                      "status": "ACTIVE"
                                    }
                                    """
                            )
                    }
            )
    )
    public ResponseEntity create(
            @Valid @RequestBody ExternalToolConfigRequest request,
            @Parameter(description = "UUID của người tạo", required = true, example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
            @RequestHeader("X-User-Id") UUID createdBy
    ) {
        return responseHandler.response(
                200,
                "Tạo cấu hình thành công",
                service.create(request, createdBy)
        );
    }
}

