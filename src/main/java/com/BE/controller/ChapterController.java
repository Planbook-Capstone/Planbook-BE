package com.BE.controller;

import com.BE.model.request.ChapterRequest;
import com.BE.model.response.ChapterResponse;
import com.BE.service.interfaceServices.IBookService;
import com.BE.service.interfaceServices.IChapterService;
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
@Tag(name = "Chapters", description = "API quản lí chương sách")
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChapterController {

    IChapterService chapterService;

    ResponseHandler responseHandler;


    // --- API: TẠO CHƯƠNG (POST) ---
    @PostMapping
    @Operation(
            summary = "Tạo một chương mới",
            description = "API này cho phép tạo một chương mới với tên và ID cuốn sách liên kết. Tên chương phải là duy nhất trong phạm vi một cuốn sách."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Thông tin chi tiết của chương cần tạo.",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ChapterRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "createChapter1ChemistryBookExample", // TÊN ĐÃ THAY ĐỔI
                                    summary = "Ví dụ tạo Chương 1 cho Sách Hóa học", // SUMMARY ĐÃ THAY ĐỔI
                                    value = "{\"name\": \"Chương 1: Nguyên tử và Bảng tuần hoàn\", \"bookId\": 3}" // VALUE ĐÃ THAY ĐỔI (ví dụ Book ID 3 cho Hóa học)
                            ),
                            @ExampleObject(
                                    name = "createChapter1MathBookExample",
                                    summary = "Ví dụ tạo Chương 1 cho Sách Toán",
                                    value = "{\"name\": \"Chương 1: Tập hợp và Các phép toán\", \"bookId\": 1}" // Giả sử Book ID 1 là "Sách giáo khoa Toán 10"
                            ),
                            @ExampleObject(
                                    name = "createChapter2PhysicsBookExample",
                                    summary = "Ví dụ tạo Chương 2 cho Sách Vật lý",
                                    value = "{\"name\": \"Chương 2: Động học chất điểm\", \"bookId\": 2}" // Giả sử Book ID 2 là "Bài tập Vật lý 10"
                            )
                    }
            )
    )
    @ApiResponse(responseCode = "201", description = "Chương được tạo thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChapterResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dữ liệu yêu cầu không hợp lệ hoặc tên chương đã tồn tại trong cuốn sách.",
            content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "invalidInput", summary = "Lỗi dữ liệu đầu vào không hợp lệ",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"Chapter name cannot be blank\",\n  \"details\": \"uri=/api/chapters\"\n}"),
                    @ExampleObject(name = "duplicateChapterName", summary = "Lỗi tên chương đã tồn tại",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"Chapter with name 'Chương 1: Tập hợp' already exists for Book ID: 1\",\n  \"details\": \"uri=/api/chapters\"\n}")
            }))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy cuốn sách.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "bookNotFound", summary = "Lỗi không tìm thấy cuốn sách",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Book not found with ID: 999\",\n  \"details\": \"uri=/api/chapters\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> createChapter(@Valid @RequestBody ChapterRequest request) {
        ChapterResponse response = chapterService.createChapter(request);
        return responseHandler.response(201, "Tạo chương thành công!", response);
    }

    // --- API: LẤY DANH SÁCH CHƯƠNG (GET All Chapters) ---
    @GetMapping
    @Operation(
            summary = "Lấy danh sách các chương",
            description = "API này hỗ trợ phân trang, tìm kiếm theo tên, lọc theo trạng thái và sắp xếp."
    )
    @ApiResponse(responseCode = "200", description = "Lấy danh sách chương thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> getAllChapters(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @Parameter(
                    description = "Trạng thái của chương để lọc. Giá trị hợp lệ: ACTIVE, INACTIVE",
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
        Page<ChapterResponse> chapterPage = chapterService.getAllChapters(pageForBackend, size, search, status, sortBy, sortDirection);
        return responseHandler.response(200, "Lấy danh sách chương thành công!", chapterPage);
    }

    // --- API: LẤY CHƯƠNG BẰNG ID (GET By ID) ---
    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin một chương theo ID",
            description = "API này trả về thông tin chi tiết của một chương dựa trên ID duy nhất của nó."
    )
    @ApiResponse(responseCode = "200", description = "Lấy thông tin chương thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChapterResponse.class)))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy chương.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "chapterNotFound", summary = "Lỗi không tìm thấy chương",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Chapter not found with ID: 99\",\n  \"details\": \"uri=/api/chapters/99\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> getChapterById(@PathVariable long id) {
        ChapterResponse response = chapterService.getChapterById(id);
        return responseHandler.response(200, "Lấy thông tin chương thành công!", response);
    }

    // --- API: CẬP NHẬT CHƯƠNG (PUT) ---
    @PutMapping("/{id}")
    @Operation(
            summary = "Cập nhật thông tin một chương",
            description = "API này cho phép cập nhật tên và ID cuốn sách của một chương. Tên chương phải là duy nhất trong phạm vi một cuốn sách."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Thông tin chi tiết của chương cần cập nhật.",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ChapterRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "updateChapterNameExample",
                                    summary = "Ví dụ cập nhật tên chương",
                                    value = "{\"name\": \"Chương 1: Các phép toán về tập hợp (Cập nhật)\", \"bookId\": 1}"
                            ),
                            @ExampleObject(
                                    name = "moveChapterToAnotherBookExample",
                                    summary = "Ví dụ di chuyển chương sang sách khác",
                                    value = "{\"name\": \"Chương 2: Động học (Cập nhật)\", \"bookId\": 3}" // Giả sử Book ID 3 là sách khác
                            )
                    }
            )
    )
    @ApiResponse(responseCode = "200", description = "Chương được cập nhật thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChapterResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dữ liệu yêu cầu không hợp lệ hoặc tên chương đã tồn tại trong cuốn sách.",
            content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "invalidInput", summary = "Lỗi dữ liệu đầu vào không hợp lệ",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"Chapter name cannot be blank\",\n  \"details\": \"uri=/api/chapters/1\"\n}"),
                    @ExampleObject(name = "duplicateChapterName", summary = "Lỗi tên chương đã tồn tại",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"Chapter with name 'Chương 1: Tập hợp' already exists for Book ID: 1\",\n  \"details\": \"uri=/api/chapters/1\"\n}")
            }))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy chương hoặc cuốn sách mới.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "chapterOrBookNotFound", summary = "Lỗi không tìm thấy chương hoặc cuốn sách",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Chapter not found with ID: 99\",\n  \"details\": \"uri=/api/chapters/99\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> updateChapter(@PathVariable long id, @Valid @RequestBody ChapterRequest request) {
        ChapterResponse response = chapterService.updateChapter(id, request);
        return responseHandler.response(200, "Cập nhật chương thành công!", response);
    }

    // --- API: CẬP NHẬT TRẠNG THÁI CHƯƠNG (PATCH Status) ---
    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Cập nhật trạng thái của chương (bao gồm vô hiệu hóa/kích hoạt)",
            description = "API này cho phép cập nhật trạng thái hoạt động của chương thành 'ACTIVE' hoặc 'INACTIVE'."
    )
    @Parameter(
            name = "newStatus",
            description = "Trạng thái mới cho chương. Giá trị hợp lệ: ACTIVE, INACTIVE",
            required = true,
            schema = @Schema(
                    type = "string",
                    allowableValues = {"ACTIVE", "INACTIVE"},
                    example = "ACTIVE"
            )
    )
    @ApiResponse(responseCode = "200", description = "Cập nhật trạng thái chương thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChapterResponse.class)))
    @ApiResponse(responseCode = "400", description = "Trạng thái không hợp lệ.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "invalidStatus", summary = "Lỗi trạng thái không hợp lệ",
                    value = "{\n  \"statusCode\": 400,\n  \"message\": \"Invalid status value. Must be 'ACTIVE' or 'INACTIVE'.\",\n  \"details\": \"uri=/api/chapters/1/status\"\n}")))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy chương.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "chapterNotFound", summary = "Lỗi không tìm thấy chương",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Chapter not found with ID: 99\",\n  \"details\": \"uri=/api/chapters/99/status\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> changeChapterStatus(
            @PathVariable long id,
            @RequestParam String newStatus) {
        ChapterResponse response = chapterService.changeChapterStatus(id, newStatus);
        return responseHandler.response(200, "Cập nhật trạng thái chương thành công!", response);
    }

    // --- API MỚI: LẤY DANH SÁCH CHƯƠNG THEO BOOK ID (GET By Book ID) ---
    @GetMapping("/by-book/{bookId}")
    @Operation(
            summary = "Lấy danh sách các chương theo ID cuốn sách",
            description = "API này trả về danh sách các chương thuộc một cuốn sách cụ thể, có hỗ trợ phân trang, tìm kiếm và lọc."
    )
    @ApiResponse(responseCode = "200", description = "Lấy danh sách chương theo cuốn sách thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy cuốn sách.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "bookNotFound", summary = "Lỗi không tìm thấy cuốn sách",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Book not found with ID: 999\",\n  \"details\": \"uri=/api/chapters/by-book/999\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> getChaptersByBookId(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @Parameter(
                    description = "Trạng thái của chương để lọc. Giá trị hợp lệ: ACTIVE, INACTIVE",
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
        Page<ChapterResponse> chapterPage = chapterService.getChaptersByBookId(bookId, pageForBackend, size, search, status, sortBy, sortDirection);
        return responseHandler.response(200, "Lấy danh sách chương theo sách thành công!", chapterPage);
    }


}