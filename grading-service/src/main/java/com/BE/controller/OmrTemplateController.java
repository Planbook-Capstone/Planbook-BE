package com.BE.controller;

import com.BE.enums.StatusEnum;
import com.BE.model.request.OmrTemplateRequest;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.OmrTemplateResponse;
import com.BE.service.interfaceServices.OmrTemplateService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/omr-templates")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@Tag(name = "OMR Template", description = "APIs for managing OMR templates")
public class OmrTemplateController {

    private final OmrTemplateService omrTemplateService;
    private final ResponseHandler responseHandler;

    @Operation(summary = "Create a new OMR template")
    @PostMapping
    public ResponseEntity<DataResponseDTO<OmrTemplateResponse>> create(@Valid @RequestBody OmrTemplateRequest request) {
        OmrTemplateResponse newTemplate = omrTemplateService.create(request);
        return responseHandler.response(HttpStatus.CREATED.value(), "Tạo mẫu OMR thành công", newTemplate);
    }

    @Operation(summary = "Get all OMR templates")
    @GetMapping
    public ResponseEntity<DataResponseDTO<List<OmrTemplateResponse>>> getAll() {
        List<OmrTemplateResponse> templates = omrTemplateService.getAll();
        return responseHandler.response(HttpStatus.OK.value(), "Lấy danh sách tất cả các mẫu OMR thành công", templates);
    }

    @Operation(summary = "Get an OMR template by ID")
    @GetMapping("/{id}")
    public ResponseEntity<DataResponseDTO<OmrTemplateResponse>> getById(@PathVariable Long id) {
        OmrTemplateResponse template = omrTemplateService.getById(id);
        return responseHandler.response(HttpStatus.OK.value(), "Lấy mẫu OMR thành công", template);
    }

    @Operation(summary = "Update an existing OMR template")
    @PutMapping("/{id}")
    public ResponseEntity<DataResponseDTO<OmrTemplateResponse>> update(@PathVariable Long id, @Valid @RequestBody OmrTemplateRequest request) {
        OmrTemplateResponse updatedTemplate = omrTemplateService.update(id, request);
        return responseHandler.response(HttpStatus.OK.value(), "Cập nhật mẫu OMR thành công", updatedTemplate);
    }

    @Operation(summary = "Delete an OMR template")
    @PatchMapping("/{id}/status")
    public ResponseEntity updateStatus(
            @PathVariable Long id,
            @Parameter(description = "Trạng thái mới omr-template", required = true, schema = @Schema(implementation = StatusEnum.class, allowableValues = {"ACTIVE", "INACTIVE"}))
            @RequestParam StatusEnum status
    ) {
        return responseHandler.response(HttpStatus.OK.value(), "Cập nhật trạng thái mẫu OMR thành công", omrTemplateService.updateStatus(id,status));
    }
}

