package com.BE.controller;

import com.BE.enums.StatusEnum;
import com.BE.model.request.OmrTemplateRequest;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.OmrTemplateResponse;
import com.BE.service.interfaceServices.OmrTemplateService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/omr-templates")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@Tag(name = "Mẫu OMR", description = "Các API để quản lý mẫu OMR")
public class OmrTemplateController {

    private final OmrTemplateService omrTemplateService;
    private final ResponseHandler responseHandler;

    @Operation(summary = "Tạo một mẫu OMR mới",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dữ liệu để tạo một mẫu OMR mới.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OmrTemplateRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Ví dụ mẫu",
                                            value = "{\n  \"name\": \"Phiếu Chuẩn 2024\",\n  \"sample_image_url\": \"https://example.com/sample.jpg\"\n}"
                                    )
                            }
                    )
            )
    )
    @PostMapping
    public ResponseEntity<DataResponseDTO<OmrTemplateResponse>> create(@Valid @org.springframework.web.bind.annotation.RequestBody OmrTemplateRequest request) {
        OmrTemplateResponse newTemplate = omrTemplateService.create(request);
        return responseHandler.response(HttpStatus.CREATED.value(), "Tạo mẫu OMR thành công", newTemplate);
    }

    @Operation(summary = "Lấy tất cả các mẫu OMR")
    @GetMapping
    public ResponseEntity<DataResponseDTO<List<OmrTemplateResponse>>> getAll() {
        List<OmrTemplateResponse> templates = omrTemplateService.getAll();
        return responseHandler.response(HttpStatus.OK.value(), "Lấy danh sách tất cả các mẫu OMR thành công", templates);
    }

    @Operation(summary = "Lấy một mẫu OMR bằng ID")
    @GetMapping("/{id}")
    public ResponseEntity<DataResponseDTO<OmrTemplateResponse>> getById(@PathVariable Long id) {
        OmrTemplateResponse template = omrTemplateService.getById(id);
        return responseHandler.response(HttpStatus.OK.value(), "Lấy mẫu OMR thành công", template);
    }

    @Operation(summary = "Cập nhật một mẫu OMR đã có",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dữ liệu để cập nhật một mẫu OMR.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OmrTemplateRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Ví dụ cập nhật",
                                            value = "{\n  \"name\": \"Phiếu Trắc Nghiệm Mới\",\n  \"sample_image_url\": \"https://example.com/new_sample.jpg\"\n}"
                                    )
                            }
                    )
            )
    )
    @PutMapping("/{id}")
    public ResponseEntity<DataResponseDTO<OmrTemplateResponse>> update(@PathVariable Long id, @Valid @org.springframework.web.bind.annotation.RequestBody OmrTemplateRequest request) {
        OmrTemplateResponse updatedTemplate = omrTemplateService.update(id, request);
        return responseHandler.response(HttpStatus.OK.value(), "Cập nhật mẫu OMR thành công", updatedTemplate);
    }

    @Operation(summary = "Cập nhật trạng thái một mẫu OMR")
    @PatchMapping("/{id}/status")
    public ResponseEntity<DataResponseDTO<OmrTemplateResponse>> updateStatus(
            @PathVariable Long id,
            @Parameter(description = "Trạng thái mới omr-template", required = true, schema = @Schema(implementation = StatusEnum.class, allowableValues = {"ACTIVE", "INACTIVE"}))
            @RequestParam StatusEnum status
    ) {
        return responseHandler.response(HttpStatus.OK.value(), "Cập nhật trạng thái mẫu OMR thành công", omrTemplateService.updateStatus(id,status));
    }
}

