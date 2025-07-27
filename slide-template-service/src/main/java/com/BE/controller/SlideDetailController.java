package com.BE.controller;

import com.BE.model.response.SlideDetailResponse;
import com.BE.service.interfaceServices.ISlideDetailService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Slide-Detail", description = "API quản lý chi tiết các slide trong template")
@RequestMapping("/api/slide-details")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SlideDetailController {

    ISlideDetailService iSlideDetailService;
    ResponseHandler responseHandler;

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin slide detail theo ID",
            description = "Truy xuất thông tin chi tiết một slide theo ID của slide."
    )
    @ApiResponse(responseCode = "200", description = "Lấy thông tin thành công")
    @ApiResponse(responseCode = "404", description = "Không tìm thấy slide detail")
    @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    public ResponseEntity<SlideDetailResponse> getSlideDetail(@PathVariable String id) {
        return responseHandler.response(200, "Lấy thông tin slide detail thành công!", 
                iSlideDetailService.getSlideDetail(id));
    }

    @GetMapping("/template/{templateId}")
    @Operation(
            summary = "Lấy danh sách slide details theo template ID",
            description = "Truy xuất danh sách tất cả slide details thuộc về một slide template."
    )
    @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    @ApiResponse(responseCode = "404", description = "Không tìm thấy template")
    @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    public ResponseEntity<List<SlideDetailResponse>> getSlideDetailsByTemplateId(@PathVariable Long templateId) {
        return responseHandler.response(200, "Lấy danh sách slide details thành công!", 
                iSlideDetailService.getSlideDetailsByTemplateId(templateId));
    }
}
