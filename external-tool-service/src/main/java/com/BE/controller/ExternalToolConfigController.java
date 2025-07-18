package com.BE.controller;

import com.BE.enums.StatusEnum;
import com.BE.model.request.ExternalToolConfigRequest;
import com.BE.model.request.ExternalToolSearchRequest;
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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/external-tools")
@SecurityRequirement(name = "api")
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
                                              "icon": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==",
                                              "clientSecret": "super-secret",
                                              "description": "Tích hợp AI để tạo văn bản",
                                              "tokenCostPerQuery": 8,
                                              "inputJson": {"className": "SE1705"}
                                            }
                                            """
                            )
                    }
            )
    )
    public ResponseEntity create(
            @Valid @RequestBody ExternalToolConfigRequest request
    ) {
        return responseHandler.response(
                200,
                "Tạo cấu hình thành công",
                service.create(request)
        );
    }


    @GetMapping
    @Operation(summary = "Lấy danh sách cấu hình công cụ bên ngoài",
            description = "Lọc theo trạng thái (ACTIVE/INACTIVE), tìm kiếm theo tên/mô tả, phân trang và sắp xếp")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExternalToolConfigResponse.class)))
    })
    public ResponseEntity<?> getAll(
            @ParameterObject ExternalToolSearchRequest request
    ) {
        Page<ExternalToolConfigResponse> externalToolConfigResponses = service.getAll(request);
        return responseHandler.response(200, "Lấy tất cả tool thành công", externalToolConfigResponses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết cấu hình công cụ theo ID",
            description = "Trả về thông tin chi tiết cấu hình công cụ bên ngoài theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy chi tiết thành công",
                    content = @Content(schema = @Schema(implementation = ExternalToolConfigResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy công cụ")
    })
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return responseHandler.response(200, "Lấy chi tiết thành công", service.getById(id));
    }


    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật cấu hình công cụ",
            description = "Cập nhật các thông tin chi tiết về công cụ bên ngoài, ngoại trừ trạng thái (status).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExternalToolConfigResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy công cụ cần cập nhật"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dữ liệu cấu hình cập nhật",
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
                                              "name": "Tạo slide bài giảng",
                                              "apiUrl": "https://api.aiwriter.com/generate",
                                              "tokenUrl": "https://api.aiwriter.com/token",
                                              "clientId": "ai-client",
                                              "clientSecret": "super-secret",
                                              "icon": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==",
                                              "description": "Tích hợp AI để tạo văn bản",
                                              "tokenCostPerQuery": 10,
                                              "inputJson": {"className": "SE1705"}
                                            }
                                            """
                            )
                    }
            )
    )
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody ExternalToolConfigRequest request
    ) {
        return responseHandler.response(200, "Cập nhật thành công", service.update(id, request));
    }


    @PatchMapping("/{id}/status")
    @Operation(summary = "Thay đổi trạng thái công cụ",
            description = "Chỉ thay đổi giữa trạng thái ACTIVE hoặc INACTIVE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật trạng thái thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy công cụ")
    })
    public ResponseEntity<?> changeStatus(
            @PathVariable Long id,
            @Parameter(
                    description = "Trạng thái mới cần cập nhật",
                    required = true,
                    schema = @Schema(
                            description = "Trạng thái hiện tại của công cụ. " +
                                    "Có thể là: PENDING (chờ duyệt), APPROVED (đã duyệt), " +
                                    "ACTIVE (đang hoạt động), INACTIVE (ngừng hoạt động), " +
                                    "REJECTED (bị từ chối), CANCELLED (bị hủy), DELETED (đã xóa).",
                            implementation = StatusEnum.class,
                            allowableValues = {
                                    "PENDING", "APPROVED", "ACTIVE", "INACTIVE",
                                    "REJECTED", "CANCELLED", "DELETED"
                            }
                    )
            )
            @RequestParam StatusEnum status
    ) {
        return responseHandler.response(200, "Cập nhật trạng thái thành công", service.updateStatus(id, status));
    }
}

