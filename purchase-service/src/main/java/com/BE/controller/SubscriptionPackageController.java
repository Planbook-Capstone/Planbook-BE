package com.BE.controller;

import com.BE.enums.SubscriptionStatus;
import com.BE.model.request.SubscriptionPackageRequest;
import com.BE.model.response.SubscriptionPackageResponse;
import com.BE.service.interfaceServices.ISubscriptionPackageService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription-packages")
@SecurityRequirement(name = "api")
public class SubscriptionPackageController {

    private final ISubscriptionPackageService service;
    private final ResponseHandler responseHandler;


    @PostMapping
    @Operation(summary = "Tạo gói token mới", description = "Tạo một gói token để người dùng có thể mua và sử dụng cho các công cụ AI.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo gói thành công", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionPackageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc tên gói đã tồn tại")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Thông tin gói token mới",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SubscriptionPackageRequest.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "name": "Starter Pack",
                                      "tokenAmount": 100,
                                      "price": 5000,
                                      "description": "Truy cập cơ bản cho người dùng mới",
                                      "highlight": false,
                                      "priority": 1,
                                      "features": {
                                        "1": "Truy cập ưu tiên vào AI",
                                        "2": "Hỗ trợ khách hàng qua email",
                                        "3": "Lịch sử trò chuyện giới hạn"
                                      }
                                    }
                                    """
                    )
            )
    )
    public ResponseEntity<?> create(@Valid @RequestBody SubscriptionPackageRequest request) {
        return responseHandler.response(200, "Tạo gói thành công", service.create(request));
    }


    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật gói token", description = "Cập nhật thông tin một gói token đã có theo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionPackageResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy gói với ID tương ứng")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Thông tin gói token cần cập nhật",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SubscriptionPackageRequest.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "name": "Pro Pack",
                                      "tokenAmount": 500,
                                      "price": 10000,
                                      "description": "Gói nâng cao với nhiều quyền lợi",
                                      "highlight": true,
                                      "priority": 1,
                                      "features": {
                                        "1": "Truy cập không giới hạn",
                                        "2": "Hỗ trợ ưu tiên",
                                        "3": "Tăng giới hạn API"
                                      }
                                    }
                                    """
                    )
            )
    )
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody SubscriptionPackageRequest request) {
        return responseHandler.response(200, "Cập nhật gói thành công", service.update(id, request));
    }


    @GetMapping
    @Operation(summary = "Lấy tất cả gói token", description = "Lọc theo trạng thái (ACTIVE/INACTIVE) và sắp xếp theo ngày tạo/cập nhật.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy thành công", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SubscriptionPackageResponse.class))))
    })
    public ResponseEntity<?> getAll(
            @Parameter(
                    description = "Trạng thái gói",
                    schema = @Schema(implementation = SubscriptionStatus.class, allowableValues = {"ACTIVE", "INACTIVE"})
            )
            @RequestParam(required = false) SubscriptionStatus status,

            @Parameter(
                    description = "Sắp xếp theo trường",
                    schema = @Schema(type = "string", allowableValues = {"createdAt", "updatedAt"})
            )
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,

            @Parameter(
                    description = "Chiều sắp xếp",
                    schema = @Schema(type = "string", allowableValues = {"asc", "desc"})
            )
            @RequestParam(required = false, defaultValue = "desc") String sortDirection
    ) {
        return responseHandler.response(200, "Lấy tất cả thành công", service.getAll(status, sortBy, sortDirection));
    }


    @GetMapping("/{id}")
    @Operation(summary = "Lấy gói token theo ID", description = "Trả về thông tin chi tiết của một gói token theo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy thành công", content = @Content(schema = @Schema(implementation = SubscriptionPackageResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy gói với ID tương ứng")
    })
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        return responseHandler.response(200, "Lấy gói theo ID thành công", service.getById(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Thay đổi trạng thái gói (ACTIVE/INACTIVE)", description = "Chuyển đổi trạng thái của gói giữa ACTIVE và INACTIVE.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Thay đổi trạng thái thành công")})
    public ResponseEntity<?> changeStatus(@PathVariable UUID id) {
        return responseHandler.response(200, "Cập nhật trạng thái thành công", service.changeStatus(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá gói token", description = "Xoá một gói token theo ID.")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Xoá thành công")})
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

