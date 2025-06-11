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
@Tag(name = "BookType", description = "API để quản lý các loại sách (ví dụ: Sách giáo khoa, Sách bài tập)")
@RequestMapping("/api/book-type")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookTypeController {

    ResponseHandler responseHandler;

    IBookTypeService bookTypeService;



    // --- API: TẠO LOẠI SÁCH (POST) ---
    @PostMapping
    @Operation(
            summary = "Tạo một loại sách mới",
            description = "API này cho phép tạo một loại sách mới với tên, mô tả và icon. Tên loại sách phải là duy nhất.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin chi tiết của loại sách cần tạo.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookTypeRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "createTextbookBookTypeExample",
                                            summary = "Ví dụ tạo Loại Sách: Sách giáo khoa",
                                            value = "{\n  \"name\": \"Sách giáo khoa\",\n  \"description\": \"Loại sách dùng cho việc học tập chính khóa trên lớp.\",\n  \"icon\": \"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==\"\n}"
                                    ),
                                    @ExampleObject(
                                            name = "createExerciseBookTypeExample",
                                            summary = "Ví dụ tạo Loại Sách: Sách bài tập",
                                            value = "{\n  \"name\": \"Sách bài tập\",\n  \"description\": \"Loại sách chứa các bài tập thực hành và nâng cao.\",\n  \"icon\": null\n}"
                                    )
                            }
                    )
            )
    )
    @ApiResponse(responseCode = "201", description = "Loại sách được tạo thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookTypeResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dữ liệu yêu cầu không hợp lệ hoặc tên loại sách đã tồn tại.",
            content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "invalidInput", summary = "Lỗi dữ liệu đầu vào không hợp lệ",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"BookType name cannot be blank\",\n  \"details\": \"uri=/api/book-type\"\n}"),
                    @ExampleObject(name = "duplicateBookTypeName", summary = "Lỗi tên loại sách đã tồn tại",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"BookType with name 'Sách giáo khoa' already exists\",\n  \"details\": \"uri=/api/book-type\"\n}")
            }))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> createBookType(@Valid @RequestBody BookTypeRequest request) {
        BookTypeResponse response = bookTypeService.createBookType(request);
        return responseHandler.response(201, "BookType created successfully!", response);
    }

    // --- API: LẤY DANH SÁCH LOẠI SÁCH (GET All BookTypes) ---
    @GetMapping
    @Operation(
            summary = "Lấy danh sách các loại sách",
            description = "API này hỗ trợ phân trang, tìm kiếm theo tên, lọc theo trạng thái và sắp xếp."
    )
    @ApiResponse(responseCode = "200", description = "Lấy danh sách loại sách thành công.",
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
                    description = "Trạng thái của loại sách để lọc. Giá trị hợp lệ: ACTIVE, INACTIVE",
                    schema = @Schema(
                            type = "string",
                            allowableValues = {"ACTIVE", "INACTIVE"},
                            example = "ACTIVE"
                    )
            )
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        int pageForBackend = page > 0 ? page - 1 : 0; // Backend page starts from 0
        Page<BookTypeResponse> bookTypePage = bookTypeService.getAllBookTypes(pageForBackend, size, search, status, sortBy, sortDirection);
        return responseHandler.response(200, "BookTypes retrieved successfully!", bookTypePage);
    }

    // --- API: LẤY LOẠI SÁCH BẰNG ID (GET By ID) ---
    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin một loại sách theo ID",
            description = "API này trả về thông tin chi tiết của một loại sách dựa trên ID duy nhất (UUID) của nó."
    )
    @Parameter(
            name = "id",
            description = "ID (UUID) của loại sách cần lấy.",
            required = true,
            schema = @Schema(type = "string", format = "uuid", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    )
    @ApiResponse(responseCode = "200", description = "Lấy thông tin loại sách thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookTypeResponse.class)))
    @ApiResponse(responseCode = "400", description = "ID không hợp lệ (không phải định dạng UUID).",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "invalidUUID", summary = "Lỗi ID không hợp lệ",
                    value = "{\n  \"statusCode\": 400,\n  \"message\": \"Invalid UUID string: 'invalid-id'\",\n  \"details\": \"uri=/api/book-type/invalid-id\"\n}")))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy loại sách.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "bookTypeNotFound", summary = "Lỗi không tìm thấy loại sách",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"BookType not found with ID: a1b2c3d4-e5f6-7890-1234-567890abcde0\",\n  \"details\": \"uri=/api/book-type/a1b2c3d4-e5f6-7890-1234-567890abcde0\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> getBookTypeById(@PathVariable UUID id) { // Tham số là UUID
        BookTypeResponse response = bookTypeService.getBookTypeById(id);
        return responseHandler.response(200, "BookType retrieved successfully!", response);
    }

    // --- API: CẬP NHẬT LOẠI SÁCH (PUT) ---
    @PutMapping("/{id}")
    @Operation(
            summary = "Cập nhật thông tin một loại sách",
            description = "API này cho phép cập nhật tên, mô tả và icon của một loại sách dựa trên ID (UUID) của nó. Tên loại sách phải là duy nhất (trừ chính nó).",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin chi tiết của loại sách cần cập nhật.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookTypeRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "updateBookTypeNameExample",
                                            summary = "Ví dụ cập nhật tên loại sách",
                                            value = "{\n  \"name\": \"Sách giáo khoa (Cập nhật)\",\n  \"description\": \"Loại sách dùng cho việc học tập chính khóa trên lớp.\",\n  \"icon\": null\n}"
                                    ),
                                    @ExampleObject(
                                            name = "updateBookTypeDescriptionAndIconExample",
                                            summary = "Ví dụ cập nhật mô tả và icon",
                                            value = "{\n  \"name\": \"Sách bài tập\",\n  \"description\": \"Sách dùng để luyện tập, củng cố kiến thức.\",\n  \"icon\": \"data:image/jpeg;base64,.....\"\n}"
                                    )
                            }
                    )
            )
    )
    @Parameter(
            name = "id",
            description = "ID (UUID) của loại sách cần cập nhật.",
            required = true,
            schema = @Schema(type = "string", format = "uuid", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    )
    @ApiResponse(responseCode = "200", description = "Loại sách được cập nhật thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookTypeResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dữ liệu yêu cầu không hợp lệ, ID không hợp lệ hoặc tên loại sách đã tồn tại.",
            content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "invalidInput", summary = "Lỗi dữ liệu đầu vào không hợp lệ",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"BookType name cannot be blank\",\n  \"details\": \"uri=/api/book-type/a1b2c3d4-e5f6-7890-1234-567890abcdef\"\n}"),
                    @ExampleObject(name = "duplicateBookTypeName", summary = "Lỗi tên loại sách đã tồn tại",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"BookType with name 'Sách giáo khoa' already exists\",\n  \"details\": \"uri=/api/book-type/a1b2c3d4-e5f6-7890-1234-567890abcdef\"\n}")
            }))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy loại sách.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "bookTypeNotFound", summary = "Lỗi không tìm thấy loại sách",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"BookType not found with ID: a1b2c3d4-e5f6-7890-1234-567890abcde0\",\n  \"details\": \"uri=/api/book-type/a1b2c3d4-e5f6-7890-1234-567890abcde0\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> updateBookType(@PathVariable UUID id, @Valid @RequestBody BookTypeRequest request) {
        BookTypeResponse response = bookTypeService.updateBookType(id, request);
        return responseHandler.response(200, "BookType updated successfully!", response);
    }

    // --- API: CẬP NHẬT TRẠNG THÁI LOẠI SÁCH (PATCH Status) ---
    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Cập nhật trạng thái của loại sách (bao gồm vô hiệu hóa/kích hoạt)",
            description = "API này cho phép cập nhật trạng thái hoạt động của loại sách thành 'ACTIVE' hoặc 'INACTIVE' dựa trên ID (UUID) của nó. Khi một loại sách bị 'INACTIVE', nó có thể không được hiển thị hoặc sử dụng trong một số chức năng của ứng dụng.",
            parameters = @Parameter(
                    name = "newStatus",
                    description = "Trạng thái mới cho loại sách. Giá trị hợp lệ: ACTIVE, INACTIVE",
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
            description = "ID (UUID) của loại sách cần cập nhật trạng thái.",
            required = true,
            schema = @Schema(type = "string", format = "uuid", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    )
    @ApiResponse(responseCode = "200", description = "Cập nhật trạng thái loại sách thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookTypeResponse.class)))
    @ApiResponse(responseCode = "400", description = "ID không hợp lệ hoặc trạng thái không hợp lệ.",
            content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "invalidUUID", summary = "Lỗi ID không hợp lệ",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"Invalid UUID string: 'invalid-id'\",\n  \"details\": \"uri=/api/book-type/invalid-id/status\"\n}"),
                    @ExampleObject(name = "invalidStatus", summary = "Lỗi trạng thái không hợp lệ",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"Invalid status value: 'PENDING'. Must be ACTIVE or INACTIVE.\",\n  \"details\": \"uri=/api/book-type/a1b2c3d4-e5f6-7890-1234-567890abcdef/status\"\n}")
            }))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy loại sách.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "bookTypeNotFound", summary = "Lỗi không tìm thấy loại sách",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"BookType not found with ID: a1b2c3d4-e5f6-7890-1234-567890abcde0\",\n  \"details\": \"uri=/api/book-type/a1b2c3d4-e5f6-7890-1234-567890abcde0/status\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> changeBookTypeStatus(
            @PathVariable UUID id,
            @RequestParam String newStatus) {
        BookTypeResponse response = bookTypeService.changeBookTypeStatus(id, newStatus);
        return responseHandler.response(200, "BookType status updated successfully!", response);
    }
}
