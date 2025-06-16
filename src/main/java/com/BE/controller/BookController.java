package com.BE.controller;


import com.BE.model.request.BookRequest;
import com.BE.model.response.BookResponse;
import com.BE.service.interfaceServices.IBookService;
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
@Tag(name = "Books", description = "API quản lí sách giáo khoa")
@RequestMapping("/api/books")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookController {


    IBookService bookService;

    ResponseHandler responseHandler;


    // --- API: TẠO SÁCH (POST) ---
    @PostMapping
    @Operation(
            summary = "Tạo một cuốn sách mới",
            description = "API này cho phép tạo một cuốn sách mới với tên và ID môn học liên kết. Tên sách phải là duy nhất trong phạm vi một môn học."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Thông tin chi tiết của cuốn sách cần tạo.",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BookRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "createMathBookExample",
                                    summary = "Ví dụ tạo sách Toán học",
                                    value = "{\"name\": \"Sách giáo khoa Toán 10\", \"subjectId\": 1}" // Giả sử Subject ID 1 là "Toán học 10"
                            ),
                            @ExampleObject(
                                    name = "createPhysicsWorkbookExample",
                                    summary = "Ví dụ tạo sách Bài tập Vật lý",
                                    value = "{\"name\": \"Bài tập Vật lý 10\", \"subjectId\": 2}" // Giả sử Subject ID 2 là "Vật lý 10"
                            ),
                            @ExampleObject(
                                    name = "createChemistryTextbookExample",
                                    summary = "Ví dụ tạo sách Hóa học",
                                    value = "{\"name\": \"Sách giáo khoa Hóa học 10\", \"subjectId\": 3}" // Giả sử Subject ID 3 là "Hóa học 10"
                            )
                    }
            )
    )
    @ApiResponse(responseCode = "201", description = "Sách được tạo thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dữ liệu yêu cầu không hợp lệ hoặc tên sách đã tồn tại trong môn học.",
            content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "invalidInput", summary = "Lỗi dữ liệu đầu vào không hợp lệ",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"Book name cannot be blank\",\n  \"details\": \"uri=/api/books\"\n}"),
                    @ExampleObject(name = "duplicateBookName", summary = "Lỗi tên sách đã tồn tại",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"Book with name 'Sách giáo khoa Toán 10' already exists for Subject ID: 1\",\n  \"details\": \"uri=/api/books\"\n}")
            }))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy môn học.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "subjectNotFound", summary = "Lỗi không tìm thấy môn học",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Subject not found with ID: 999\",\n  \"details\": \"uri=/api/books\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> createBook(@Valid @RequestBody BookRequest request) {
        BookResponse response = bookService.createBook(request);
        return responseHandler.response(201, "Book created successfully!", response);
    }

    // --- API: LẤY DANH SÁCH SÁCH (GET All Books) ---
    @GetMapping
    @Operation(
            summary = "Lấy danh sách các cuốn sách",
            description = "API này hỗ trợ phân trang, tìm kiếm theo tên, lọc theo trạng thái và sắp xếp."
    )
    @ApiResponse(responseCode = "200", description = "Lấy danh sách sách thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> getAllBooks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @Parameter(
                    description = "Trạng thái của cuốn sách để lọc. Giá trị hợp lệ: ACTIVE, INACTIVE",
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
        Page<BookResponse> bookPage = bookService.getAllBooks(pageForBackend, size, search, status, sortBy, sortDirection);
        return responseHandler.response(200, "Books retrieved successfully!", bookPage);
    }

    // --- API: LẤY SÁCH BẰNG ID (GET By ID) ---
    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin một cuốn sách theo ID",
            description = "API này trả về thông tin chi tiết của một cuốn sách dựa trên ID duy nhất của nó."
    )
    @ApiResponse(responseCode = "200", description = "Lấy thông tin sách thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponse.class)))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy cuốn sách.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "bookNotFound", summary = "Lỗi không tìm thấy sách",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Book not found with ID: 99\",\n  \"details\": \"uri=/api/books/99\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> getBookById(@PathVariable long id) {
        BookResponse response = bookService.getBookById(id);
        return responseHandler.response(200, "Book retrieved successfully!", response);
    }

    // --- API: CẬP NHẬT SÁCH (PUT) ---
    @PutMapping("/{id}")
    @Operation(
            summary = "Cập nhật thông tin một cuốn sách",
            description = "API này cho phép cập nhật tên và ID môn học của một cuốn sách. Tên sách phải là duy nhất trong phạm vi một môn học."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Thông tin chi tiết của cuốn sách cần cập nhật.",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BookRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "updateMathBookNameExample",
                                    summary = "Ví dụ cập nhật tên sách Toán",
                                    value = "{\"name\": \"Sách giáo khoa Toán 10 Nâng cao\", \"subjectId\": 1}"
                            ),
                            @ExampleObject(
                                    name = "moveBookToAnotherSubjectExample",
                                    summary = "Ví dụ di chuyển sách sang môn học khác",
                                    value = "{\"name\": \"Sách giáo khoa Hóa học 10\", \"subjectId\": 4}" // Giả sử Subject ID 4 là môn "Hóa học 11"
                            )
                    }
            )
    )
    @ApiResponse(responseCode = "200", description = "Sách được cập nhật thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dữ liệu yêu cầu không hợp lệ hoặc tên sách đã tồn tại trong môn học.",
            content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "invalidInput", summary = "Lỗi dữ liệu đầu vào không hợp lệ",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"Book name cannot be blank\",\n  \"details\": \"uri=/api/books/1\"\n}"),
                    @ExampleObject(name = "duplicateBookName", summary = "Lỗi tên sách đã tồn tại",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"Book with name 'Sách giáo khoa Toán 10' already exists for Subject ID: 1\",\n  \"details\": \"uri=/api/books/1\"\n}")
            }))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy cuốn sách hoặc môn học mới.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "bookOrSubjectNotFound", summary = "Lỗi không tìm thấy sách hoặc môn học",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Book not found with ID: 99\",\n  \"details\": \"uri=/api/books/99\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> updateBook(@PathVariable long id, @Valid @RequestBody BookRequest request) {
        BookResponse response = bookService.updateBook(id, request);
        return responseHandler.response(200, "Book updated successfully!", response);
    }

    // --- API: CẬP NHẬT TRẠNG THÁI SÁCH (PATCH Status) ---
    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Cập nhật trạng thái của cuốn sách (bao gồm vô hiệu hóa/kích hoạt)",
            description = "API này cho phép cập nhật trạng thái hoạt động của cuốn sách thành 'ACTIVE' hoặc 'INACTIVE'."
    )
    @Parameter(
            name = "newStatus",
            description = "Trạng thái mới cho cuốn sách. Giá trị hợp lệ: ACTIVE, INACTIVE",
            required = true,
            schema = @Schema(
                    type = "string",
                    allowableValues = {"ACTIVE", "INACTIVE"},
                    example = "ACTIVE"
            )
    )
    @ApiResponse(responseCode = "200", description = "Cập nhật trạng thái sách thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponse.class)))
    @ApiResponse(responseCode = "400", description = "Trạng thái không hợp lệ.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "invalidStatus", summary = "Lỗi trạng thái không hợp lệ",
                    value = "{\n  \"statusCode\": 400,\n  \"message\": \"Invalid status value. Must be 'ACTIVE' or 'INACTIVE'.\",\n  \"details\": \"uri=/api/books/1/status\"\n}")))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy cuốn sách.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "bookNotFound", summary = "Lỗi không tìm thấy sách",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Book not found with ID: 99\",\n  \"details\": \"uri=/api/books/99/status\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> changeBookStatus(
            @PathVariable long id,
            @RequestParam String newStatus) {
        BookResponse response = bookService.changeBookStatus(id, newStatus);
        return responseHandler.response(200, "Book status updated successfully!", response);
    }

    // --- API MỚI: LẤY DANH SÁCH SÁCH THEO SUBJECT ID (GET By Subject ID) ---
    @GetMapping("/by-subject/{subjectId}")
    @Operation(
            summary = "Lấy danh sách các cuốn sách theo ID môn học",
            description = "API này trả về danh sách các cuốn sách thuộc một môn học cụ thể, có hỗ trợ phân trang, tìm kiếm và lọc."
    )
    @ApiResponse(responseCode = "200", description = "Lấy danh sách sách theo môn học thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy môn học.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "subjectNotFound", summary = "Lỗi không tìm thấy môn học",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Subject not found with ID: 999\",\n  \"details\": \"uri=/api/books/by-subject/999\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> getBooksBySubjectId(
            @PathVariable Long subjectId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @Parameter(
                    description = "Trạng thái của cuốn sách để lọc. Giá trị hợp lệ: ACTIVE, INACTIVE",
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
        Page<BookResponse> bookPage = bookService.getBooksBySubjectId(subjectId, pageForBackend, size, search, status, sortBy, sortDirection);
        return responseHandler.response(200, "Books retrieved successfully for Subject ID: " + subjectId, bookPage);
    }


}
