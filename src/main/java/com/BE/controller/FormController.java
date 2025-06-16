package com.BE.controller;

import com.BE.model.entity.Form;
import com.BE.model.request.FormRequest;
import com.BE.model.response.FormResponse;
import com.BE.service.interfaceServices.IFormService;
import com.BE.utils.ResponseHandler;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Forms", description = "API for form Lesson Plan")
@RequestMapping("/api/forms")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FormController {

    private final IFormService formService;
    private final ResponseHandler responseHandler;

    @PostMapping
    @Operation(
            summary = "Tạo mới khung mẫu giáo án",
            description = "API này tạo một khung mẫu giáo án mới với thông tin được cung cấp. " +
                    "Biểu mẫu bao gồm tên, mô tả và dữ liệu khung mẫu được định dạng JSON.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Thông tin khung mẫu cần tạo",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FormRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "createBasicFormExample",
                                            summary = "Ví dụ tạo khung mẫu cơ bản",
                                            value = "{\"name\": \"Mẫu khung giáo án chuẩn\", \"description\": \"khung mẫu chuẩn cho giáo án\", \"status\": \"DRAFT\", \"formData\": [{\"group_name\": \"Thông tin chung\", \"fields\": [{\"field_name\": \"Tên bài học\", \"data_type\": \"string\", \"is_required\": true, \"fields\": []}]}, {\"group_name\": \"Nội dung\", \"fields\": []}]}"
                                    )

                            }
                    )
            )
    )
    @ApiResponse(responseCode = "200", description = "khung mẫu được tạo thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Form.class)))
    @ApiResponse(responseCode = "400", description = "Dữ liệu yêu cầu không hợp lệ.",
            content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "invalidInput", summary = "Lỗi dữ liệu đầu vào không hợp lệ",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"Name cannot be blank\",\n  \"details\": \"uri=/api/form\"\n}"),
                    @ExampleObject(name = "invalidFormData", summary = "Lỗi dữ liệu biểu mẫu không hợp lệ",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"FormData cannot be blank\",\n  \"details\": \"uri=/api/form\"\n}")
            }))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Form> saveForm(@Valid @RequestBody FormRequest request) {
        return responseHandler.response(200, "Form save successfully!", formService.saveForm(request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin khung mẫu giáo án theo ID",
            description = "API này truy xuất thông tin chi tiết của một khung mẫu giáo án dựa trên ID. " +
                    "Kết quả bao gồm đầy đủ thông tin như tên, mô tả, dữ liệu khung mẫu và thời gian tạo/cập nhật."
    )
    @ApiResponse(responseCode = "200", description = "Lấy thông tin khung mẫu thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = FormResponse.class),
                    examples = {
                            @ExampleObject(
                                    name = "formResponseExample",
                                    summary = "Ví dụ phản hồi thành công",
                                    value = "{\n  \"statusCode\": 200,\n  \"message\": \"Form retrieved successfully!\",\n  \"data\": {\n    \"id\": 1,\n    \"name\": \"Mẫu giáo án chuẩn\",\n    \"description\": \"Biểu mẫu chuẩn cho giáo án\",\n    \"formData\": [{\"group_name\": \"Thông tin chung\", \"fields\": [{\"field_name\": \"Tên bài học\", \"data_type\": \"string\", \"is_required\": true, \"fields\": []}]}, {\"group_name\": \"Nội dung\", \"fields\": []}],\n    \"createdAt\": \"2025-06-14T10:30:00\",\n    \"updatedAt\": \"2025-06-14T10:30:00\"\n  }\n}"
                            )
                    }))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy khung mẫu với ID đã cung cấp.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "formNotFound", summary = "Lỗi không tìm thấy khung mẫu",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Form not found with ID: 999\",\n  \"details\": \"uri=/api/form/999\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<FormResponse> getForm(@PathVariable Long id) {
        return responseHandler.response(200, "Form retrieved successfully!", formService.getForm(id));
    }

    @GetMapping
    @Operation(
            summary = "Lấy tất cả khung mẫu giáo án",
            description = "API này truy xuất danh sách tất cả các khung mẫu giáo án. " +
                    "Kết quả bao gồm danh sách đầy đủ thông tin các khung mẫu như tên, mô tả, dữ liệu khung mẫu và thời gian tạo/cập nhật."
    )
    @ApiResponse(responseCode = "200", description = "Lấy danh sách khung mẫu thành công.",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<List<FormResponse>> getAllForms() {
        return responseHandler.response(200, "Forms retrieved successfully!", formService.getAllForms());
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Cập nhật khung mẫu giáo án",
            description = "API này cập nhật thông tin của một khung mẫu giáo án dựa trên ID. " +
                    "Kết quả bao gồm thông tin đã được cập nhật như tên, mô tả, dữ liệu khung mẫu.",
            parameters = @Parameter(name = "id", description = "ID của khung mẫu cần cập nhật", required = true)
    )
    @ApiResponse(responseCode = "200", description = "Cập nhật khung mẫu thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = FormResponse.class),
                    examples = {
                            @ExampleObject(
                                    name = "formUpdateResponseExample",
                                    summary = "Ví dụ phản hồi cập nhật thành công",
                                    value = "{\n  \"statusCode\": 200,\n  \"message\": \"Form updated successfully!\",\n  \"data\": {\n    \"id\": 1,\n    \"name\": \"Mẫu giáo án chuẩn (đã cập nhật)\",\n    \"description\": \"Biểu mẫu chuẩn cho giáo án đã được điều chỉnh\",\n    \"formData\": [{\"group_name\": \"Thông tin chung\", \"fields\": [{\"field_name\": \"Tên bài học\", \"data_type\": \"string\", \"is_required\": true, \"fields\": []}]}, {\"group_name\": \"Nội dung\", \"fields\": []}],\n    \"createdAt\": \"2025-06-14T10:30:00\",\n    \"updatedAt\": \"2025-06-16T15:45:00\"\n  }\n}"
                            )
                    }))
    @ApiResponse(responseCode = "400", description = "Dữ liệu yêu cầu không hợp lệ.",
            content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "invalidUpdateInput", summary = "Lỗi dữ liệu đầu vào không hợp lệ",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"Name cannot be blank\",\n  \"details\": \"uri=/api/form/1\"\n}"),
                    @ExampleObject(name = "invalidUpdateFormData", summary = "Lỗi dữ liệu biểu mẫu không hợp lệ",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"FormData cannot be blank\",\n  \"details\": \"uri=/api/form/1\"\n}")
            }))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy khung mẫu với ID đã cung cấp.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "updateFormNotFound", summary = "Lỗi không tìm thấy khung mẫu để cập nhật",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Form not found with ID: 999\",\n  \"details\": \"uri=/api/form/999\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<FormResponse> updateForm(@PathVariable Long id, @Valid @RequestBody FormRequest request) {
        return responseHandler.response(200, "Form updated successfully!", formService.updateForm(id, request));
    }
}
