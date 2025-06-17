package com.BE.controller;

import com.BE.model.request.BookTypeRequest;
import com.BE.model.response.BookTypeResponse;
import com.BE.service.interfaceServices.IBookTypeService;
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

import java.util.UUID;

@RestController
@Tag(name = "BookTypes", description = "API để quản lý loại công cụ hỗ trợ (ví dụ: Tạo giáo án, Chấm điểm, ...)")
@RequestMapping("/api/book-types")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookTypeController {

    ResponseHandler responseHandler;

    IBookTypeService bookTypeService;


    // --- API: TẠO LOẠI CÔNG CỤ (POST) ---
    @PostMapping
    @Operation(
            summary = "Tạo một loại công cụ hỗ trợ giảng viên mới",
            description = "API này cho phép tạo một loại công cụ hỗ trợ giảng viên mới với tên, mô tả, icon và chi phí token. Tên công cụ phải là duy nhất.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin chi tiết của loại công cụ cần tạo.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookTypeRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "createGradingToolExample",
                                            summary = "Ví dụ tạo Loại Công cụ: Chấm điểm",
                                            value = "{\n  \"name\": \"Chấm điểm\",\n  \"description\": \"Công cụ giúp giảng viên quản lý và chấm điểm bài tập, bài kiểm tra của sinh viên.\",\n \"priority\": 1,\n \"href\": \"example\",\n  \"icon\": \"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==\",\n  \"tokenCostPerQuery\": 5\n}" // ĐÃ THÊM tokenCostPerQuery
                                    ),
                                    @ExampleObject(
                                            name = "createLessonPlanToolExample",
                                            summary = "Ví dụ tạo Loại Công cụ: Tạo giáo án",
                                            value = "{\n  \"name\": \"Tạo giáo án\",\n  \"description\": \"Công cụ hỗ trợ xây dựng và quản lý các giáo án điện tử.\",\n \"priority\": 1,\n  \"icon\": null,\n  \"tokenCostPerQuery\": 10\n}" // ĐÃ THÊM tokenCostPerQuery
                                    ),
                                    @ExampleObject(
                                            name = "createSlideToolExample",
                                            summary = "Ví dụ tạo Loại Công cụ: Tạo slide bài giảng",
                                            value = "{\n  \"name\": \"Tạo slide bài giảng\",\n  \"description\": \"Công cụ giúp thiết kế và trình bày slide bài giảng một cách chuyên nghiệp.\",\n \"priority\": 1,\n  \"icon\": null,\n  \"tokenCostPerQuery\": 15\n}" // ĐÃ THÊM tokenCostPerQuery
                                    ),
                                    @ExampleObject(
                                            name = "createExamToolExample",
                                            summary = "Ví dụ tạo Loại Công cụ: Tạo đề thi",
                                            value = "{\n  \"name\": \"Tạo đề thi\",\n  \"description\": \"Công cụ hỗ trợ tạo và quản lý các đề thi trắc nghiệm, tự luận.\",\n \"priority\": 1,\n  \"icon\": null,\n  \"tokenCostPerQuery\": 20\n}" // ĐÃ THÊM tokenCostPerQuery
                                    )
                            }
                    )
            )
    )
    @ApiResponse(responseCode = "201", description = "Loại công cụ được tạo thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookTypeResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dữ liệu yêu cầu không hợp lệ hoặc tên loại công cụ đã tồn tại.",
            content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "invalidInput", summary = "Lỗi dữ liệu đầu vào không hợp lệ",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"BookType name cannot be blank\",\n  \"details\": \"uri=/api/book-type\"\n}"),
                    @ExampleObject(name = "duplicateBookTypeName", summary = "Lỗi tên loại công cụ đã tồn tại",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"BookType with name 'Chấm điểm' already exists\",\n  \"details\": \"uri=/api/book-type\"\n}")
            }))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> createBookType(@Valid @RequestBody BookTypeRequest request) {
        BookTypeResponse response = bookTypeService.createBookType(request);
        return responseHandler.response(201, "Tạo loại công cụ thành công!", response);
    }

    // --- API: LẤY DANH SÁCH LOẠI CÔNG CỤ (GET All BookTypes) ---
    @GetMapping
    @Operation(
            summary = "Lấy danh sách các loại công cụ hỗ trợ giảng viên",
            description = "API này hỗ trợ phân trang, tìm kiếm theo tên, lọc theo trạng thái và sắp xếp."
    )
    @ApiResponse(responseCode = "200", description = "Lấy danh sách loại công cụ thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "400", description = "Giá trị tham số không hợp lệ.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "invalidParams", summary = "Lỗi tham số phân trang/sắp xếp",
                    value = "{\n  \"statusCode\": 400,\n  \"message\": \"Invalid status value: INVALID_STATUS. Must be ACTIVE or INACTIVE.\",\n  \"details\": \"uri=/api/book-type\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> getAllBookTypes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @Parameter(
                    description = "Trạng thái của loại công cụ để lọc. Giá trị hợp lệ: ACTIVE, INACTIVE",
                    schema = @Schema(
                            type = "string",
                            allowableValues = {"ACTIVE", "INACTIVE"},
                            example = "ACTIVE"
                    )
            )
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        int pageForBackend = page > 0 ? page - 1 : 0;
        Page<BookTypeResponse> bookTypePage = bookTypeService.getAllBookTypes(pageForBackend, size, search, status, sortBy, sortDirection);
        return responseHandler.response(200, "Lấy thông tin tất cả các loại công cụ hỗ trợ thành công!", bookTypePage);
    }

    // --- API: LẤY LOẠI CÔNG CỤ BẰNG ID (GET By ID) ---
    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin một loại công cụ hỗ trợ giảng viên theo ID",
            description = "API này trả về thông tin chi tiết của một loại công cụ dựa trên ID duy nhất (UUID) của nó."
    )
    @Parameter(
            name = "id",
            description = "ID (UUID) của loại công cụ cần lấy.",
            required = true,
            schema = @Schema(type = "string", format = "uuid", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    )
    @ApiResponse(responseCode = "200", description = "Lấy thông tin loại công cụ thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookTypeResponse.class)))
    @ApiResponse(responseCode = "400", description = "ID không hợp lệ (không phải định dạng UUID).",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "invalidUUID", summary = "Lỗi ID không hợp lệ",
                    value = "{\n  \"statusCode\": 400,\n  \"message\": \"Invalid UUID string: 'invalid-id'\",\n  \"details\": \"uri=/api/book-type/invalid-id\"\n}")))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy loại công cụ.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "bookTypeNotFound", summary = "Lỗi không tìm thấy loại công cụ",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"BookType not found with ID: a1b2c3d4-e5f6-7890-1234-567890abcde0\",\n  \"details\": \"uri=/api/book-type/a1b2c3d4-e5f6-7890-1234-567890abcde0\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> getBookTypeById(@PathVariable UUID id) {
        BookTypeResponse response = bookTypeService.getBookTypeById(id);
        return responseHandler.response(200, "Lấy thông tin loại công cụ thành công!", response);
    }

    // --- API: CẬP NHẬT LOẠI CÔNG CỤ (PUT) ---
    @PutMapping("/{id}")
    @Operation(
            summary = "Cập nhật thông tin một loại công cụ hỗ trợ giảng viên",
            description = "API này cho phép cập nhật tên, mô tả, icon và chi phí token của một loại công cụ dựa trên ID (UUID) của nó. Tên công cụ phải là duy nhất (trừ chính nó).",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin chi tiết của loại công cụ cần cập nhật.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookTypeRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "updateGradingToolExample", // ĐÃ THAY ĐỔI TÊN EXAMPLE
                                            summary = "Ví dụ cập nhật công cụ 'Chấm điểm' (cả token)",
                                            value = "{\n  \"name\": \"Chấm điểm (Đã nâng cấp)\",\n  \"description\": \"Công cụ giúp giảng viên quản lý và chấm điểm bài tập, bài kiểm tra của sinh viên, với nhiều tính năng mới.\",\n \"priority\": 1,\n  \"icon\": null,\n  \"tokenCostPerQuery\": 7\n}" // ĐÃ THÊM tokenCostPerQuery và sửa giá trị
                                    ),
                                    @ExampleObject(
                                            name = "updateLessonPlanToolExample", // ĐÃ THAY ĐỔI TÊN EXAMPLE
                                            summary = "Ví dụ cập nhật công cụ 'Tạo giáo án' (cả token)",
                                            value = "{\n  \"name\": \"Tạo giáo án\",\n  \"description\": \"Công cụ số hóa và quản lý giáo án điện tử toàn diện.\",\n \"priority\": 1,\n  \"icon\": \"data:image/jpeg;base64,.....\",\n  \"tokenCostPerQuery\": 12\n}" // ĐÃ THÊM tokenCostPerQuery và sửa giá trị
                                    )
                            }
                    )
            )
    )
    @Parameter(
            name = "id",
            description = "ID (UUID) của loại công cụ cần cập nhật.",
            required = true,
            schema = @Schema(type = "string", format = "uuid", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    )
    @ApiResponse(responseCode = "200", description = "Loại công cụ được cập nhật thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookTypeResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dữ liệu yêu cầu không hợp lệ, ID không hợp lệ hoặc tên loại công cụ đã tồn tại.",
            content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "invalidInput", summary = "Lỗi dữ liệu đầu vào không hợp lệ",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"BookType name cannot be blank\",\n  \"details\": \"uri=/api/book-type/a1b2c3d4-e5f6-7890-1234-567890abcdef\"\n}"),
                    @ExampleObject(name = "duplicateBookTypeName", summary = "Lỗi tên loại công cụ đã tồn tại",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"BookType with name 'Chấm điểm' already exists\",\n  \"details\": \"uri=/api/book-type/a1b2c3d4-e5f6-7890-1234-567890abcdef\"\n}")
            }))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy loại công cụ.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "bookTypeNotFound", summary = "Lỗi không tìm thấy loại công cụ",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"BookType not found with ID: a1b2c3d4-e5f6-7890-1234-567890abcde0\",\n  \"details\": \"uri=/api/book-type/a1b2c3d4-e5f6-7890-1234-567890abcde0\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> updateBookType(@PathVariable UUID id, @Valid @RequestBody BookTypeRequest request) {
        BookTypeResponse response = bookTypeService.updateBookType(id, request);
        return responseHandler.response(200, "Cập nhật loại công cụ thành công!", response);
    }

    // --- API: CẬP NHẬT TRẠNG THÁI LOẠI CÔNG CỤ (PATCH Status) ---
    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Cập nhật trạng thái của loại công cụ hỗ trợ giảng viên (bao gồm vô hiệu hóa/kích hoạt)",
            description = "API này cho phép cập nhật trạng thái hoạt động của loại công cụ thành 'ACTIVE' hoặc 'INACTIVE' dựa trên ID (UUID) của nó. Khi một loại công cụ bị 'INACTIVE', nó có thể không được hiển thị hoặc sử dụng trong một số chức năng của ứng dụng.",
            parameters = @Parameter(
                    name = "newStatus",
                    description = "Trạng thái mới cho loại công cụ. Giá trị hợp lệ: ACTIVE, INACTIVE",
                    required = true,
                    schema = @Schema(
                            type = "string",
                            allowableValues = {"ACTIVE", "INACTIVE"},
                            example = "ACTIVE"
                    )
            )
    )
    @Parameter(
            name = "id",
            description = "ID (UUID) của loại công cụ cần cập nhật trạng thái.",
            required = true,
            schema = @Schema(type = "string", format = "uuid", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    )
    @ApiResponse(responseCode = "200", description = "Cập nhật trạng thái loại công cụ thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookTypeResponse.class)))
    @ApiResponse(responseCode = "400", description = "ID không hợp lệ hoặc trạng thái không hợp lệ.",
            content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "invalidUUID", summary = "Lỗi ID không hợp lệ",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"Invalid UUID string: 'invalid-id'\",\n  \"details\": \"uri=/api/book-type/invalid-id/status\"\n}"),
                    @ExampleObject(name = "invalidStatus", summary = "Lỗi trạng thái không hợp lệ",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"Invalid status value: 'PENDING'. Must be ACTIVE or INACTIVE.\",\n  \"details\": \"uri=/api/book-type/a1b2c3d4-e5f6-7890-1234-567890abcdef/status\"\n}")
            }))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy loại công cụ.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "bookTypeNotFound", summary = "Lỗi không tìm thấy loại công cụ",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"BookType not found with ID: a1b2c3d4-e5f6-7890-1234-567890abcde0\",\n  \"details\": \"uri=/api/book-type/a1b2c3d4-e5f6-7890-1234-567890abcde0/status\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> changeBookTypeStatus(
            @PathVariable UUID id,
            @RequestParam String newStatus) {
        BookTypeResponse response = bookTypeService.changeBookTypeStatus(id, newStatus);
        return responseHandler.response(200, "Trạng thái công cụ cập nhật thành công !", response);
    }
}
