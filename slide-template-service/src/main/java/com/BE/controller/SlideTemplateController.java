package com.BE.controller;

import com.BE.model.entity.SlideTemplate;
import com.BE.model.request.SlideTemplateRequest;
import com.BE.model.response.SlideTemplateResponse;
import com.BE.service.interfaceServices.ISlideTemplateService;
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
@Tag(name = "Slide-Template", description = "API quản lý các mẫu thuyết trình (slide templates)")
@RequestMapping("/api/slide-templates")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SlideTemplateController {

    private final ISlideTemplateService iSlideTemplateService;
    private final ResponseHandler responseHandler;

    @PostMapping
    @Operation(
            summary = "Tạo mới slide template",
            description = "Tạo một mẫu thuyết trình mới với thông tin gồm tên, mô tả, trạng thái và các khối dữ liệu văn bản/hình ảnh.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Dữ liệu slide template cần tạo",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SlideTemplateRequest.class),
                            examples = {@ExampleObject(
                                    name = "createSlideTemplateExample",
                                    summary = "Ví dụ tạo slide template",
                                    value = """
                                            {
                                              "name": "Mẫu giáo án chuẩn",
                                              "description": "Mẫu chuẩn dành cho lớp học STEM",                                      
                                              "textBlocks": {
                                                "title": "Giới thiệu bài học",
                                                "content": "Bài học về kỹ năng mềm"
                                              },
                                              "imageBlocks": {
                                                "logo": "https://example.com/images/logo.png",
                                                "cover": "https://example.com/images/cover.jpg"
                                              }
                                            }
                                            """
                            )}
                    )
            )
    )
    @ApiResponse(responseCode = "200", description = "Tạo slide template thành công",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SlideTemplate.class)))
    @ApiResponse(responseCode = "400", description = "Dữ liệu yêu cầu không hợp lệ",
            content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "invalidInput", summary = "Tên không hợp lệ",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"Name cannot be blank\",\n  \"details\": \"uri=/api/slide-templates\"\n}"),
                    @ExampleObject(name = "invalidFormData", summary = "Khối dữ liệu không hợp lệ",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"TextBlocks cannot be blank\",\n  \"details\": \"uri=/api/slide-templates\"\n}")
            }))
    @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    public ResponseEntity<SlideTemplate> saveForm(@Valid @RequestBody SlideTemplateRequest request) {
        return responseHandler.response(200, "Tạo slide template thành công!", iSlideTemplateService.saveSlideTemplate(request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin slide template theo ID",
            description = "Truy xuất thông tin chi tiết một slide template bao gồm tên, mô tả, trạng thái, dữ liệu văn bản và hình ảnh."
    )
    @ApiResponse(responseCode = "200", description = "Lấy thông tin slide template thành công",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SlideTemplateResponse.class)))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy slide template",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    public ResponseEntity<SlideTemplateResponse> getForm(@PathVariable Long id) {
        return responseHandler.response(200, "Lấy thông tin slide template thành công!", iSlideTemplateService.getSlideTemplate(id));
    }

    @GetMapping
    @Operation(
            summary = "Lấy danh sách slide templates",
            description = "Hỗ trợ phân trang, tìm kiếm theo tên, lọc theo trạng thái (ACTIVE/INACTIVE) và sắp xếp."
    )
    @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    public ResponseEntity<Object> getAllSlideTemplates(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @Parameter(description = "Trạng thái để lọc (ACTIVE, INACTIVE)",
                    schema = @Schema(type = "string", allowableValues = {"ACTIVE", "INACTIVE"}))
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {

        Page<SlideTemplateResponse> slideTemplatePage = iSlideTemplateService.getAllSlideTemplates(
                page, size, search, status, sortBy, sortDirection);

        return responseHandler.response(200, "Lấy danh sách slide templates thành công!", slideTemplatePage);
    }


    @PutMapping("/{id}")
    @Operation(
            summary = "Cập nhật slide template",
            description = "Cập nhật thông tin một slide template dựa trên ID. Bao gồm tên, mô tả, trạng thái và nội dung dữ liệu.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Dữ liệu cần cập nhật",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SlideTemplateRequest.class),
                            examples = @ExampleObject(
                                    name = "updateSlideTemplateExample",
                                    summary = "Ví dụ cập nhật slide template",
                                    value = """
                                            {
                                              \"name\": \"Mẫu giáo án nâng cao\",
                                              \"description\": \"Cập nhật mô tả và hình ảnh cho bài học kỹ năng mềm\",
                                              \"textBlocks\": {
                                                \"title\": \"Bài học kỹ năng nâng cao\",
                                                \"note\": \"Dành cho học sinh lớp 9 trở lên\"
                                              },
                                              \"imageBlocks\": {
                                                \"logo\": \"https://example.com/images/new-logo.png\",
                                                \"cover\": \"https://example.com/images/new-cover.jpg\"
                                              }
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponse(responseCode = "200", description = "Cập nhật slide template thành công",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SlideTemplateResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy slide template",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    public ResponseEntity<SlideTemplateResponse> updateForm(
            @PathVariable Long id,
            @Valid @RequestBody SlideTemplateRequest request
    ) {
        return responseHandler.response(200, "Cập nhật slide template thành công!", iSlideTemplateService.updateSlideTemplate(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Thay đổi trạng thái slide template",
            description = "Cập nhật trạng thái hoạt động của slide template thành ACTIVE hoặc INACTIVE."
    )
    @Parameter(name = "newStatus", description = "Trạng thái mới (ACTIVE hoặc INACTIVE)",
            required = true, schema = @Schema(type = "string", allowableValues = {"ACTIVE", "INACTIVE"}))
    @ApiResponse(responseCode = "200", description = "Cập nhật trạng thái thành công",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SlideTemplateResponse.class)))
    @ApiResponse(responseCode = "400", description = "Trạng thái không hợp lệ",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy slide template",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    public ResponseEntity<Object> changeSlideTemplateStatus(
            @PathVariable long id,
            @RequestParam String newStatus) {
        SlideTemplateResponse response = iSlideTemplateService.changeSlideTemplateStatus(id, newStatus);
        return responseHandler.response(200, "Cập nhật trạng thái slide template thành công!", response);
    }
}
