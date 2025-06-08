package com.BE.controller;


import com.BE.model.request.GradeRequest;
import com.BE.model.response.GradeResponse;
import com.BE.service.interfaceServices.IGradeService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Grade", description = "API for managing Grade")
@RequestMapping("/api/grade")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GradeController {

    IGradeService gradeService;

    ResponseHandler responseHandler;

    @PostMapping
    @Operation(
            summary = "Tạo mới một khối lớp",
            description = "API này cho phép tạo mới một khối lớp trong hệ thống. " +
                    "Chỉ cần cung cấp tên khối lớp là đủ để tạo. " +
                    "Hệ thống sẽ tự động gán ID và các thông tin mặc định khác (ví dụ: trạng thái ACTIVE, thời gian tạo/cập nhật)." +
                    "Đảm bảo tên khối lớp là duy nhất và không bị trùng lặp với các khối lớp hiện có."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody( // Đây là annotation của Swagger để mô tả request body
            description = "Thông tin chi tiết của khối lớp cần tạo. " +
                    "Dữ liệu yêu cầu phải là JSON với trường 'name' không được để trống.",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GradeRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "createGrade10Example",
                                    summary = "Ví dụ tạo khối lớp 10",
                                    value = "{\"name\": \"Lớp 10\"}"
                            ),
                            @ExampleObject(
                                    name = "createGrade11Example",
                                    summary = "Ví dụ tạo khối lớp 11",
                                    value = "{\"name\": \"Lớp 11\"}"
                            ),
                            @ExampleObject(
                                    name = "createGrade12Example",
                                    summary = "Ví dụ tạo khối lớp 12",
                                    value = "{\"name\": \"Lớp 12\"}"
                            )
                    }
            )
    )
    @ApiResponse(
            responseCode = "200",
            description = "Tạo khối lớp thành công. Trả về thông tin chi tiết của khối lớp vừa được tạo, bao gồm ID, tên, trạng thái và thời gian tạo/cập nhật.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GradeResponse.class),
                    examples = @ExampleObject(
                            name = "gradeSuccessResponse",
                            summary = "Phản hồi thành công khi tạo khối lớp",
                            value = "{\n" +
                                    "  \"statusCode\": 200,\n" +
                                    "  \"message\": \"Create Grade success!\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"id\": 1,\n" +
                                    "    \"name\": \"Lớp 10\",\n" +
                                    "    \"status\": \"ACTIVE\",\n" +
                                    "    \"createdAt\": \"2025-06-08T10:00:00Z\",\n" +
                                    "    \"updatedAt\": \"2025-06-08T10:00:00Z\"\n" +
                                    "  }\n" +
                                    "}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Yêu cầu không hợp lệ (ví dụ: 'name' bị trống, hoặc tên khối lớp đã tồn tại).",
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "badRequestValidationExample",
                                    summary = "Lỗi validation: Tên bị trống",
                                    value = "{\n" +
                                            "  \"statusCode\": 400,\n" +
                                            "  \"message\": \"Validation failed: Grade name cannot be blank\",\n" +
                                            "  \"details\": \"uri=/api/grades\"\n" +
                                            "}"
                            ),
                            @ExampleObject(
                                    name = "badRequestDuplicateNameExample",
                                    summary = "Lỗi nghiệp vụ: Tên khối lớp đã tồn tại",
                                    value = "{\n" +
                                            "  \"statusCode\": 400,\n" +
                                            "  \"message\": \"Grade with name 'Lớp 10' already exists.\",\n" +
                                            "  \"details\": \"uri=/api/grades\"\n" +
                                            "}"
                            )
                    }
            )
    )
    @ApiResponse(
            responseCode = "500",
            description = "Lỗi máy chủ nội bộ. Vui lòng liên hệ quản trị viên.",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "internalServerError",
                            summary = "Lỗi server nội bộ",
                            value = "{\n" +
                                    "  \"statusCode\": 500,\n" +
                                    "  \"message\": \"Internal Server Error\",\n" +
                                    "  \"details\": \"An unexpected error occurred.\"\n" +
                                    "}"
                    )
            )
    )
    public ResponseEntity<GradeResponse> createGrade(
            @Valid // Annotation của Jakarta Validation để kích hoạt kiểm tra ràng buộc
            @RequestBody // Annotation của Spring MVC để bind request body
            GradeRequest request) {
        GradeResponse response = gradeService.createGrade(request);
        return responseHandler.response(200, "Create Grade success!", response);
    }





}
