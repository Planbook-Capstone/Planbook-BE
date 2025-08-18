package com.BE.controller;

import com.BE.enums.StatusEnum;
import com.BE.model.request.MatrixConfigRequest;
import com.BE.service.interfaceService.IMatrixConfigService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "api")
@RequestMapping("/api/matrix-configs")
@RequiredArgsConstructor
@Tag(name = "Cấu hình ma trận đề thi", description = "API quản lý cấu hình ma trận đề thi (matrixConfig)")
public class MatrixConfigController {

    private final IMatrixConfigService service;
    private final ResponseHandler responseHandler;

    @PostMapping
    @Operation(
            summary = "Tạo mới cấu hình ma trận",
            description = "Tạo mới 1 bản cấu hình matrix để sử dụng cho việc sinh đề.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin cấu hình ma trận cần tạo",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Ví dụ cấu hình matrix",
                                    summary = "Matrix cấu hình",
                                    value = """
                                            {
                                              "name": "Cấu hình ma trận THPT",
                                              "description": "Áp dụng cho đề thi THPT",
                                              "matrixJson": {
                                                "parts": [
                                                  {
                                                    "id": "part1",
                                                    "name": "Phần 1",
                                                    "label": "Trắc nghiệm",
                                                    "color": "bg-amber-50",
                                                    "difficultyLevels": [
                                                      { "id": "nb", "name": "NB", "label": "Nhận biết", "color": "text-amber-700" },
                                                      { "id": "th", "name": "TH", "label": "Thông hiểu", "color": "text-amber-700" },
                                                      { "id": "vd", "name": "VD", "label": "Vận dụng", "color": "text-amber-700" }
                                                    ]
                                                  },
                                                  {
                                                    "id": "part2",
                                                    "name": "Phần 2",
                                                    "label": "Đúng/Sai",
                                                    "color": "bg-green-50",
                                                    "difficultyLevels": [
                                                      { "id": "nb", "name": "NB", "label": "Nhận biết", "color": "text-green-700" },
                                                      { "id": "th", "name": "TH", "label": "Thông hiểu", "color": "text-green-700" },
                                                      { "id": "vd", "name": "VD", "label": "Vận dụng", "color": "text-green-700" }
                                                    ]
                                                  }
                                                ]
                                              }
                                            }
                                            """
                            )
                    )
            )
    )
    public ResponseEntity<?> create(@Valid @RequestBody MatrixConfigRequest request) {
        return responseHandler.response(200, "Tạo cấu hình thành công", service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Cập nhật cấu hình",
            description = "Cập nhật thông tin cấu hình ma trận theo ID.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin mới để cập nhật",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Ví dụ cập nhật",
                                    summary = "Cập nhật matrix THCS",
                                    value = """
                                            {
                                              "name": "Cấu hình THCS",
                                              "description": "Dành cho đề THCS",
                                              "matrixJson": {
                                                "parts": [
                                                  {
                                                    "id": "part3",
                                                    "name": "Phần 3",
                                                    "label": "Tự luận",
                                                    "color": "bg-sky-50",
                                                    "difficultyLevels": [
                                                      { "id": "nb", "name": "NB", "label": "Nhận biết", "color": "text-sky-700" },
                                                      { "id": "th", "name": "TH", "label": "Thông hiểu", "color": "text-sky-700" },
                                                      { "id": "vd", "name": "VD", "label": "Vận dụng", "color": "text-sky-700" }
                                                    ]
                                                  }
                                                ]
                                              }
                                            }
                                            """
                            )
                    )
            )
    )
    public ResponseEntity update(
            @PathVariable @Parameter(description = "ID của cấu hình cần cập nhật") Long id,
            @Valid @RequestBody @Parameter(description = "Thông tin mới để cập nhật") MatrixConfigRequest request) {
        return responseHandler.response(200, "Cập nhật cấu hình thành công", service.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Cập nhật trạng thái ACTIVE/INACTIVE", description = "Chỉ được phép có duy nhất 1 cấu hình ACTIVE tại một thời điểm.")
    public ResponseEntity updateStatus(
            @PathVariable @Parameter(description = "ID của cấu hình") Long id,
            @RequestParam @Parameter(description = "Trạng thái mới: ACTIVE hoặc INACTIVE", schema = @Schema(implementation = StatusEnum.class))
            StatusEnum status) {
        return responseHandler.response(200, "Cập nhật trạng thái thành công", service.updateStatus(id, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy cấu hình theo ID", description = "Trả về chi tiết cấu hình matrix theo ID.")
    public ResponseEntity getById(
            @PathVariable @Parameter(description = "ID của cấu hình") Long id) {
        return responseHandler.response(200, "Lấy thông tin thành công", service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách cấu hình theo trạng thái", description = "Trả về danh sách cấu hình có trạng thái ACTIVE, INACTIVE hoặc tất cả nếu không truyền.")
    public ResponseEntity<?> getAllByStatus(
            @RequestParam(required = false)
            @Parameter(description = "Trạng thái cần lọc: ACTIVE hoặc INACTIVE", schema = @Schema(implementation = StatusEnum.class))
            StatusEnum status) {

        return responseHandler.response(200, "Danh sách theo trạng thái", service.getAllByStatus(status));
    }

}
