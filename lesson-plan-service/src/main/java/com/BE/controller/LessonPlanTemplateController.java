package com.BE.controller;

import com.BE.enums.Status;
import com.BE.model.response.LessonPlanTemplateDTO;
import com.BE.model.request.CreateLessonPlanRequest;
import com.BE.model.request.UpdateLessonPlanRequest;
import com.BE.service.interfaceServices.LessonPlanTemplateService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



/**
 * REST Controller for LessonPlan operations
 */
@RestController
@RequestMapping("/api/lesson-plan-templates")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Lesson Plan Template Controller", description = "Quản lý giáo án")
public class LessonPlanTemplateController {

    private final LessonPlanTemplateService lessonPlanTemplateService;
    private final ResponseHandler responseHandler;

    @PostMapping
    @Operation(summary = "Tạo giáo án mới", description = "Tạo một giáo án mới")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo giáo án thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    // @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity createLessonPlanTemplate(@Valid @RequestBody CreateLessonPlanRequest request) {
        try {
            LessonPlanTemplateDTO lessonPlan = lessonPlanTemplateService.createLessonPlan(request);
            return responseHandler.response(200, "Tạo giáo án thành công!", lessonPlan);
        } catch (Exception e) {
            return responseHandler.response(500, "Lỗi khi tạo giáo án: " + e.getMessage(), null);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin giáo án", description = "Lấy thông tin chi tiết của một giáo án")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy thông tin thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy giáo án"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    // @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity getLessonPlanTemplate(@PathVariable Long id) {
        try {
            LessonPlanTemplateDTO lessonPlan = lessonPlanTemplateService.getLessonPlanById(id);
            return responseHandler.response(200, "Lấy thông tin giáo án thành công!", lessonPlan);
        } catch (Exception e) {
            return responseHandler.response(500, "Lỗi khi lấy thông tin giáo án: " + e.getMessage(), null);
        }
    }



    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật giáo án", description = "Cập nhật thông tin của một giáo án")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy giáo án"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    // @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity updateLessonPlanTemplate(@PathVariable Long id, @Valid @RequestBody UpdateLessonPlanRequest request) {
        try {
            LessonPlanTemplateDTO lessonPlan = lessonPlanTemplateService.updateLessonPlan(id, request);
            return responseHandler.response(200, "Cập nhật giáo án thành công!", lessonPlan);
        } catch (Exception e) {
            return responseHandler.response(500, "Lỗi khi cập nhật giáo án: " + e.getMessage(), null);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa giáo án", description = "Xóa một giáo án")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy giáo án"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    // @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity deleteLessonPlanTemplate(@PathVariable Long id) {
        try {
            lessonPlanTemplateService.deleteLessonPlan(id);
            return responseHandler.response(200, "Xóa giáo án thành công!", null);
        } catch (Exception e) {
            return responseHandler.response(500, "Lỗi khi xóa giáo án: " + e.getMessage(), null);
        }
    }





    @GetMapping
    @Operation(summary = "Lấy danh sách giáo án với phân trang", description = "Lấy tất cả giáo án với tìm kiếm, lọc trạng thái và phân trang. Page index bắt đầu từ 1.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    // @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity getAllLessonPlanTemplates(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            // Convert 1-based page index to 0-based for Spring Data
            int zeroBasedPage = Math.max(0, page - 1);
            // Sort by createdAt descending (newest first)
            Pageable pageable = PageRequest.of(zeroBasedPage, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<LessonPlanTemplateDTO> lessonPlans = lessonPlanTemplateService.getAllLessonPlans(keyword, status, pageable);
            return responseHandler.response(200, "Lấy danh sách giáo án thành công!", lessonPlans);
        } catch (Exception e) {
            return responseHandler.response(500, "Lỗi khi lấy danh sách giáo án: " + e.getMessage(), null);
        }
    }


    @PatchMapping("/{id}/status")
    @Operation(summary = "Cập nhật trạng thái ACTIVE/INACTIVE", description = "Cập nhật trạng thái mẫu giáo án")
    public ResponseEntity updateStatus(
            @PathVariable @Parameter(description = "ID của mẫu giáo án") Long id,
            @RequestParam @Parameter(description = "Trạng thái mới: ACTIVE hoặc INACTIVE", schema = @Schema(implementation = Status.class))
            Status status) {
        return responseHandler.response(200, "Cập nhật trạng thái thành công", lessonPlanTemplateService.updateStatus(id, status));
    }


}
