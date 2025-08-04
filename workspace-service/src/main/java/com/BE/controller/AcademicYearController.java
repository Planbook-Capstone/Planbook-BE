package com.BE.controller;

import com.BE.enums.AcademicYearStatusEnum;
import com.BE.model.request.AcademicYearRequest;
import com.BE.model.response.AcademicYearResponse;
import com.BE.service.interfaceServices.IAcademicYearService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

@RestController
@Tag(name = "AcademicYears", description = "API quản lý năm học")
@RequestMapping("/api/academic-years")
@SecurityRequirement(name = "api")
public class AcademicYearController {
    @Autowired
    private IAcademicYearService academicYearService;
    @Autowired
    private ResponseHandler responseHandler;

    @Operation(summary = "Lấy tất cả năm học", description = "Lấy danh sách tất cả các năm học.")
    @ApiResponse(responseCode = "200", description = "Danh sách năm học.")
    @GetMapping
    public ResponseEntity getAll() {
        return responseHandler.response(200, "Lấy tất cả năm học thành công!", academicYearService.getAll());
    }

    @Operation(
            summary = "Tạo mới năm học",
            description = "Tạo mới năm học. Trạng thái mặc định là UPCOMING; Sẽ tự động tạo workspace cho tất cả người dùng chưa có workspace cho năm này.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dữ liệu tạo năm học mới",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AcademicYearRequest.class),
                            examples = @ExampleObject(value = "{\"startDate\":\"2025-09-01T00:00:00\",\"endDate\":\"2026-05-31T00:00:00\"}")
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tạo thành công.",
                    content = @Content(
                            schema = @Schema(implementation = AcademicYearResponse.class),
                            examples = @ExampleObject(value = "{\"id\":\"uuid\",\"startDate\":\"2025-06-05T00:00:00\",\"endDate\":\"2026-06-01T00:00:00\",\"yearLabel\":\"2025-2026\",\"status\":\"UPCOMING\",\"createdAt\":\"2024-06-01T10:00:00\",\"updatedAt\":\"2024-06-01T10:00:00\"}")
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc thiếu trường bắt buộc.")
    })
    @PostMapping
    public ResponseEntity create(@Valid @RequestBody AcademicYearRequest request) {
        return responseHandler.response(200, "Tạo năm học thành công!", academicYearService.create(request));
    }

    @Operation(summary = "Cập nhật năm học", description = "Cập nhật thông tin năm học theo id. Trạng thái tự động đổi thành INACTIVE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công.", content = @Content(schema = @Schema(implementation = AcademicYearResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy năm học.")
    })
    @PutMapping("/{id}")
    public ResponseEntity update(
            @PathVariable Long id,
            @Valid @RequestBody AcademicYearRequest request) {
        return responseHandler.response(200, "Cập nhật năm học thành công!", academicYearService.update(id, request));
    }

    @Operation(
            summary = "Cập nhật trạng thái năm học",
            description = """
                    Cập nhật trạng thái năm học (ACTIVE, INACTIVE, UPCOMING).
                    - Chỉ được phép có 1 năm học ACTIVE tại một thời điểm.
                    - Nếu muốn kích hoạt năm mới, phải dừng kích hoạt năm hiện tại trước (chuyển về INACTIVE hoặc UPCOMING).
                    - Nếu đã có năm học ACTIVE khác, hệ thống sẽ báo lỗi.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công.", content = @Content(schema = @Schema(implementation = AcademicYearResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy năm học."),
            @ApiResponse(responseCode = "400", description = "Trạng thái không hợp lệ.")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @Parameter(description = "Trạng thái mới (ACTIVE, INACTIVE, UPCOMING)", required = true)
            @RequestParam AcademicYearStatusEnum status) {

        return responseHandler.response(
                200,
                "Cập nhật trạng thái năm học thành công!",
                academicYearService.updateStatus(id, status)
        );
    }


    @Operation(summary = "Xóa năm học", description = "Xóa năm học theo id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xóa thành công."),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy năm học.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        academicYearService.delete(id);
        return responseHandler.response(200, "Xóa năm học thành công!", 1);
    }

    @Operation(summary = "Lấy năm học đang hoạt động", description = "Lấy năm học đang hoạt động (ACTIVE).")
    @ApiResponse(responseCode = "200", description = "Năm học đang hoạt động.")
    @GetMapping("/active")
    public ResponseEntity getActiveAcademicYear() {
        var activeYear = academicYearService.getActiveAcademicYear();
        return responseHandler.response(200, "Lấy năm học đang hoạt động thành công!", activeYear);
    }
}