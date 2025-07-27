package com.BE.controller;

import com.BE.model.entity.SlidePlaceholder;
import com.BE.model.request.SlidePlaceholderRequest;
import com.BE.model.response.SlidePlaceholderResponse;
import com.BE.service.interfaceServices.ISlidePlaceholderService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Slide-Placeholder", description = "API quản lý các placeholder cho slide template")
@RequestMapping("/api/slide-placeholders")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SlidePlaceholderController {

    ISlidePlaceholderService iSlidePlaceholderService;
    ResponseHandler responseHandler;

    @PostMapping
    @Operation(
            summary = "Tạo mới slide placeholder",
            description = "Tạo một placeholder mới cho slide template với thông tin loại, tên và mô tả."
    )
    @ApiResponse(responseCode = "200", description = "Tạo thành công")
    @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    public ResponseEntity<SlidePlaceholder> savePlaceholder(@Valid @RequestBody SlidePlaceholderRequest request) {
        return responseHandler.response(200, "Tạo slide placeholder thành công!", 
                iSlidePlaceholderService.saveSlidePlaceholder(request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin slide placeholder theo ID",
            description = "Truy xuất thông tin chi tiết một slide placeholder theo ID."
    )
    @ApiResponse(responseCode = "200", description = "Lấy thông tin thành công")
    @ApiResponse(responseCode = "404", description = "Không tìm thấy placeholder")
    @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    public ResponseEntity<SlidePlaceholderResponse> getPlaceholder(@PathVariable Long id) {
        return responseHandler.response(200, "Lấy thông tin placeholder thành công!", 
                iSlidePlaceholderService.getSlidePlaceholder(id));
    }

    @GetMapping
    @Operation(
            summary = "Lấy danh sách tất cả slide placeholders",
            description = "Truy xuất danh sách tất cả slide placeholders có trong hệ thống."
    )
    @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    public ResponseEntity<List<SlidePlaceholderResponse>> getAllPlaceholders() {
        return responseHandler.response(200, "Lấy danh sách placeholder thành công!", 
                iSlidePlaceholderService.getAllSlidePlaceholders());
    }

    @GetMapping("/paged")
    @Operation(
            summary = "Lấy danh sách slide placeholders có phân trang",
            description = "Truy xuất danh sách slide placeholders với phân trang, tìm kiếm và lọc."
    )
    @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    public ResponseEntity<Object> getAllPlaceholdersPaged(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @Parameter(description = "Trạng thái để lọc (ACTIVE, INACTIVE)")
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {

        Page<SlidePlaceholderResponse> result = iSlidePlaceholderService.getAllSlidePlaceholders(
                page, size, search, status, sortBy, sortDirection);
        return responseHandler.response(200, "Lấy danh sách placeholder thành công!", result);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Cập nhật slide placeholder",
            description = "Cập nhật thông tin một slide placeholder dựa trên ID."
    )
    @ApiResponse(responseCode = "200", description = "Cập nhật thành công")
    @ApiResponse(responseCode = "404", description = "Không tìm thấy placeholder")
    @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    public ResponseEntity<SlidePlaceholderResponse> updatePlaceholder(
            @PathVariable Long id, @Valid @RequestBody SlidePlaceholderRequest request) {
        return responseHandler.response(200, "Cập nhật placeholder thành công!", 
                iSlidePlaceholderService.updateSlidePlaceholder(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Thay đổi trạng thái slide placeholder",
            description = "Thay đổi trạng thái (ACTIVE/INACTIVE) của một slide placeholder."
    )
    @ApiResponse(responseCode = "200", description = "Thay đổi trạng thái thành công")
    @ApiResponse(responseCode = "404", description = "Không tìm thấy placeholder")
    @ApiResponse(responseCode = "400", description = "Trạng thái không hợp lệ")
    @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    public ResponseEntity<SlidePlaceholderResponse> changeStatus(
            @PathVariable Long id, @RequestParam String status) {
        return responseHandler.response(200, "Thay đổi trạng thái placeholder thành công!", 
                iSlidePlaceholderService.changeSlidePlaceholderStatus(id, status));
    }
}
