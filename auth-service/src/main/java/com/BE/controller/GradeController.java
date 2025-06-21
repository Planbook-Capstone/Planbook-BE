package com.BE.controller;


import com.BE.enums.StatusEnum;
import com.BE.model.request.GradeRequest;
import com.BE.model.request.StatusRequest;
import com.BE.model.response.GradeResponse;
import com.BE.service.interfaceServices.IGradeService;
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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Grades", description = "API quản lí lớp học")
@RequestMapping("/api/grades")
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
            @Valid
            @RequestBody
            GradeRequest request) {
        GradeResponse response = gradeService.createGrade(request);
        return responseHandler.response(200, "Tạo khối lớp thành công!", response);
    }


    // --- API: LẤY DANH SÁCH KHỐI LỚP (GET All Grades) ---
    @GetMapping
    @Operation(
            summary = "Lấy danh sách các khối lớp",
            description = "API này hỗ trợ phân trang (pagination), tìm kiếm theo tên (search by name), " +
                    "lọc theo trạng thái (filter by status) và sắp xếp (sort by field and direction). " +
                    "Nếu các tham số tìm kiếm/lọc rỗng, nó sẽ trả về tất cả các khối lớp." +
                    "**Số trang bắt đầu từ 1 (page=1)**, kích thước trang mặc định là 10 (size=10). " +
                    "Hệ thống sẽ tự động điều chỉnh số trang để phù hợp với quy ước bắt đầu từ 0 của backend."
    )
    @ApiResponse(responseCode = "200", description = "Lấy danh sách khối lớp thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class),
                    examples = @ExampleObject(name = "gradesListSuccessResponse", summary = "Phản hồi danh sách khối lớp thành công (có phân trang)",
                            value = "{\n  \"statusCode\": 200,\n  \"message\": \"Grades retrieved successfully!\",\n  \"data\": {\n" +
                                    "    \"content\": [\n" +
                                    "      { \"id\": 1, \"name\": \"Lớp 10\", \"status\": \"ACTIVE\", \"createdAt\": \"2025-06-08T10:00:00Z\", \"updatedAt\": \"2025-06-08T10:00:00Z\" },\n" +
                                    "      { \"id\": 2, \"name\": \"Lớp 11\", \"status\": \"ACTIVE\", \"createdAt\": \"2025-06-08T10:05:00Z\", \"updatedAt\": \"2025-06-08T10:05:00Z\" }\n" +
                                    "    ],\n" +
                                    "    \"pageable\": {\"pageNumber\": 0, \"pageSize\": 10, \"sort\": {\"empty\": true, \"sorted\": false, \"unsorted\": true}, \"offset\": 0, \"paged\": true, \"unpaged\": false},\n" +
                                    "    \"last\": true,\n" +
                                    "    \"totalElements\": 2,\n" +
                                    "    \"totalPages\": 1,\n" +
                                    "    \"size\": 10,\n" +
                                    "    \"number\": 0,\n" +
                                    "    \"sort\": {\"empty\": true, \"sorted\": false, \"unsorted\": true},\n" +
                                    "    \"first\": true,\n" +
                                    "    \"numberOfElements\": 2,\n" +
                                    "    \"empty\": false\n" +
                                    "  }\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> getAllGrades(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @Parameter(
                    description = "Trạng thái của khối lớp để lọc. Giá trị hợp lệ: ACTIVE, INACTIVE",
                    schema = @Schema(
                            type = "string",
                            allowableValues = {"ACTIVE", "INACTIVE"},
                            example = "ACTIVE"
                    )
            )
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {

        int pageForBackend = page > 0 ? page - 1 : 0;

        Page<GradeResponse> gradePage = gradeService.getAllGrades(pageForBackend, size, search, status, sortBy, sortDirection);
        return responseHandler.response(200, "Lấy danh sách khối lớp thành công!", gradePage);
    }

    // --- API: LẤY KHỐI LỚP THEO ID (GET Grade By ID) ---
    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin khối lớp theo ID",
            description = "API này dùng để lấy chi tiết thông tin của một khối lớp dựa trên ID duy nhất của nó."
    )
    @ApiResponse(responseCode = "200", description = "Lấy thông tin khối lớp thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GradeResponse.class),
                    examples = @ExampleObject(name = "getGradeByIdSuccessResponse", summary = "Phản hồi thành công khi lấy khối lớp theo ID",
                            value = "{\n  \"statusCode\": 200,\n  \"message\": \"Grade retrieved successfully!\",\n  \"data\": {\n    \"id\": 1,\n    \"name\": \"Lớp 10\",\n    \"status\": \"ACTIVE\",\n    \"createdAt\": \"2025-06-08T10:00:00Z\",\n    \"updatedAt\": \"2025-06-08T10:00:00Z\"\n  }\n}")))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy khối lớp với ID đã cung cấp.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "gradeNotFound", summary = "Lỗi không tìm thấy",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Grade not found with ID: 99\",\n  \"details\": \"uri=/api/grades/99\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> getGradeById(@PathVariable long id) {
        GradeResponse response = gradeService.getGradeById(id);
        return responseHandler.response(200, "Lấy thông tin khối lớp thành công!", response);
    }

    // --- API: CẬP NHẬT TÊN KHỐI LỚP (UPDATE Name) ---
    @PutMapping("/{id}")
    @Operation(
            summary = "Cập nhật tên khối lớp",
            description = "API này cho phép cập nhật tên của một khối lớp hiện có bằng cách cung cấp ID và tên mới. " +
                    "Tên mới không được trùng với tên của khối lớp khác và không được để trống."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Thông tin mới của khối lớp. Chỉ cần cung cấp tên khối lớp để cập nhật. Tên không được để trống.",
            required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GradeRequest.class),
                    examples = @ExampleObject(name = "updateGradeNameExample", summary = "Ví dụ cập nhật tên khối lớp", value = "{\"name\": \"Lớp 10 Cập nhật\"}")))
    @ApiResponse(responseCode = "200", description = "Cập nhật khối lớp thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GradeResponse.class),
                    examples = @ExampleObject(name = "updateGradeSuccessResponse", summary = "Phản hồi thành công khi cập nhật",
                            value = "{\n  \"statusCode\": 200,\n  \"message\": \"Grade updated successfully!\",\n  \"data\": {\n    \"id\": 1,\n    \"name\": \"Lớp 10 Cập nhật\",\n    \"status\": \"ACTIVE\",\n    \"createdAt\": \"2025-06-08T10:00:00Z\",\n    \"updatedAt\": \"2025-06-08T10:30:00Z\"\n  }\n}")))
    @ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ (tên trống, tên trùng).",
            content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "updateBadRequestValidation", summary = "Lỗi validation khi cập nhật", value = "{\n  \"statusCode\": 400,\n  \"message\": \"Validation failed: Grade name cannot be blank\",\n  \"details\": \"uri=/api/grades/1\"\n}"),
                    @ExampleObject(name = "updateBadRequestDuplicateName", summary = "Lỗi trùng tên khi cập nhật", value = "{\n  \"statusCode\": 400,\n  \"message\": \"Grade with name 'Lớp 11' already exists.\",\n  \"details\": \"uri=/api/grades/1\"\n}")}))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy khối lớp để cập nhật.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "updateGradeNotFound", summary = "Lỗi không tìm thấy khi cập nhật",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Grade not found with ID: 99\",\n  \"details\": \"uri=/api/grades/99\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> updateGrade(@PathVariable long id, @Valid @RequestBody GradeRequest request) {
        GradeResponse response = gradeService.updateGrade(id, request);
        return responseHandler.response(200, "Cập nhật khối lớp thành công!", response);
    }


    // --- API: CẬP NHẬT TRẠNG THÁI KHỐI LỚP (PATCH Status) ---
    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Cập nhật trạng thái của khối lớp (bao gồm vô hiệu hóa/kích hoạt)",
            description = "API này cho phép cập nhật trạng thái hoạt động của khối lớp thành 'ACTIVE' hoặc 'INACTIVE'. " +
                    "Sử dụng query parameter 'newStatus' để truyền trạng thái mới."
    )
// ĐẶT @Parameter NGAY TRƯỚC @RequestParam
    @Parameter(
            name = "newStatus", // QUAN TRỌNG: Chỉ định rõ tên tham số mà @Parameter này mô tả
            description = "Trạng thái mới cho khối lớp. Giá trị hợp lệ: ACTIVE, INACTIVE",
            required = true,
            schema = @Schema(
                    type = "string",
                    allowableValues = {"ACTIVE", "INACTIVE"},
                    example = "ACTIVE"
            )
    )
    @ApiResponse(responseCode = "200", description = "Cập nhật trạng thái khối lớp thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GradeResponse.class),
                    examples = @ExampleObject(name = "statusUpdateSuccessResponse", summary = "Phản hồi thành công khi cập nhật trạng thái",
                            value = "{\n  \"statusCode\": 200,\n  \"message\": \"Grade status updated successfully!\",\n  \"data\": {\n    \"id\": 1,\n    \"name\": \"Lớp 10\",\n    \"status\": \"ACTIVE\",\n    \"createdAt\": \"2025-06-08T10:00:00Z\",\n    \"updatedAt\": \"2025-06-08T10:40:00Z\"\n  }\n}")))
    @ApiResponse(responseCode = "400", description = "Trạng thái không hợp lệ.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "invalidStatus", summary = "Lỗi trạng thái không hợp lệ",
                    value = "{\n  \"statusCode\": 400,\n  \"message\": \"Invalid status value. Must be 'ACTIVE' or 'INACTIVE'.\",\n  \"details\": \"uri=/api/grades/1/status\"\n}")))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy khối lớp.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "statusUpdateNotFound", summary = "Lỗi không tìm thấy khi cập nhật trạng thái",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Grade not found with ID: 99\",\n  \"details\": \"uri=/api/grades/99/status\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> updateGradeStatus(
            @PathVariable long id,
            @RequestParam String newStatus) {
        GradeResponse response = gradeService.changeGradeStatus(id, newStatus);
        return responseHandler.response(200, "Cập nhật trạng thái khối lớp thành công!", response);
    }


}