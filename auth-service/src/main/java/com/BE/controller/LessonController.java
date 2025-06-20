package com.BE.controller;

import com.BE.model.request.LessonRequest;
import com.BE.model.response.LessonResponse;
import com.BE.service.interfaceServices.ILessonService;
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
@Tag(name = "Lesson", description = "API for managing Lesson")
@RequestMapping("/api/lesson")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonController {

    ResponseHandler responseHandler;
    ILessonService lessonService;


    // --- API: TẠO BÀI HỌC (POST) ---
    @PostMapping
    @Operation(
            summary = "Tạo một bài học mới",
            description = "API này cho phép tạo một bài học mới với tên và ID chương liên kết. Tên bài học phải là duy nhất trong phạm vi một chương."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Thông tin chi tiết của bài học cần tạo.",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LessonRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "createLesson1Chapter1ChemistryExample",
                                    summary = "Ví dụ tạo Bài 1 cho Chương 1 Sách Hóa học",
                                    value = "{\"name\": \"Bài 1: Cấu tạo nguyên tử và nguyên tố hóa học\", \"chapterId\": 1}" // Giả sử Chapter ID 1 là "Chương 1: Nguyên tử và Bảng tuần hoàn"
                            ),
                            @ExampleObject(
                                    name = "createLesson2Chapter1ChemistryExample",
                                    summary = "Ví dụ tạo Bài 2 cho Chương 1 Sách Hóa học",
                                    value = "{\"name\": \"Bài 2: Liên kết hóa học\", \"chapterId\": 1}"
                            ),
                            @ExampleObject(
                                    name = "createLesson1Chapter2MathBookExample",
                                    summary = "Ví dụ tạo Bài 1 cho Chương 2 Sách Toán",
                                    value = "{\"name\": \"Bài 1: Hàm số và Đồ thị\", \"chapterId\": 2}" // Giả sử Chapter ID 2 là một chương sách Toán
                            )
                    }
            )
    )
    @ApiResponse(responseCode = "201", description = "Bài học được tạo thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LessonResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dữ liệu yêu cầu không hợp lệ hoặc tên bài học đã tồn tại trong chương.",
            content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "invalidInput", summary = "Lỗi dữ liệu đầu vào không hợp lệ",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"Lesson name cannot be blank\",\n  \"details\": \"uri=/api/lessons\"\n}"),
                    @ExampleObject(name = "duplicateLessonName", summary = "Lỗi tên bài học đã tồn tại",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"Lesson with name 'Bài 1: Cấu tạo nguyên tử' already exists for Chapter ID: 1\",\n  \"details\": \"uri=/api/lessons\"\n}")
            }))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy chương.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "chapterNotFound", summary = "Lỗi không tìm thấy chương",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Chapter not found with ID: 999\",\n  \"details\": \"uri=/api/lessons\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> createLesson(@Valid @RequestBody LessonRequest request) {
        LessonResponse response = lessonService.createLesson(request);
        return responseHandler.response(201, "Lesson created successfully!", response);
    }

    // --- API: LẤY DANH SÁCH BÀI HỌC (GET All Lessons) ---
    @GetMapping
    @Operation(
            summary = "Lấy danh sách các bài học",
            description = "API này hỗ trợ phân trang, tìm kiếm theo tên, lọc theo trạng thái và sắp xếp."
    )
    @ApiResponse(responseCode = "200", description = "Lấy danh sách bài học thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> getAllLessons(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @Parameter(
                    description = "Trạng thái của bài học để lọc. Giá trị hợp lệ: ACTIVE, INACTIVE",
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
        Page<LessonResponse> lessonPage = lessonService.getAllLessons(pageForBackend, size, search, status, sortBy, sortDirection);
        return responseHandler.response(200, "Lessons retrieved successfully!", lessonPage);
    }

    // --- API: LẤY BÀI HỌC BẰNG ID (GET By ID) ---
    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin một bài học theo ID",
            description = "API này trả về thông tin chi tiết của một bài học dựa trên ID duy nhất của nó."
    )
    @ApiResponse(responseCode = "200", description = "Lấy thông tin bài học thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LessonResponse.class)))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy bài học.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "lessonNotFound", summary = "Lỗi không tìm thấy bài học",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Lesson not found with ID: 99\",\n  \"details\": \"uri=/api/lessons/99\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> getLessonById(@PathVariable long id) {
        LessonResponse response = lessonService.getLessonById(id);
        return responseHandler.response(200, "Lesson retrieved successfully!", response);
    }

    // --- API: CẬP NHẬT BÀI HỌC (PUT) ---
    @PutMapping("/{id}")
    @Operation(
            summary = "Cập nhật thông tin một bài học",
            description = "API này cho phép cập nhật tên và ID chương của một bài học. Tên bài học phải là duy nhất trong phạm vi một chương."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Thông tin chi tiết của bài học cần cập nhật.",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LessonRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "updateLessonNameExample",
                                    summary = "Ví dụ cập nhật tên bài học",
                                    value = "{\"name\": \"Bài 1: Cấu tạo nguyên tử (Cập nhật)\", \"chapterId\": 1}"
                            ),
                            @ExampleObject(
                                    name = "moveLessonToAnotherChapterExample",
                                    summary = "Ví dụ di chuyển bài học sang chương khác",
                                    value = "{\"name\": \"Bài 3: Định luật Newton\", \"chapterId\": 3}" // Giả sử Chapter ID 3 là chương khác
                            )
                    }
            )
    )
    @ApiResponse(responseCode = "200", description = "Bài học được cập nhật thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LessonResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dữ liệu yêu cầu không hợp lệ hoặc tên bài học đã tồn tại trong chương.",
            content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "invalidInput", summary = "Lỗi dữ liệu đầu vào không hợp lệ",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"Lesson name cannot be blank\",\n  \"details\": \"uri=/api/lessons/1\"\n}"),
                    @ExampleObject(name = "duplicateLessonName", summary = "Lỗi tên bài học đã tồn tại",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"Lesson with name 'Bài 1: Cấu tạo nguyên tử' already exists for Chapter ID: 1\",\n  \"details\": \"uri=/api/lessons/1\"\n}")
            }))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy bài học hoặc chương mới.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "lessonOrChapterNotFound", summary = "Lỗi không tìm thấy bài học hoặc chương",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Lesson not found with ID: 99\",\n  \"details\": \"uri=/api/lessons/99\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> updateLesson(@PathVariable long id, @Valid @RequestBody LessonRequest request) {
        LessonResponse response = lessonService.updateLesson(id, request);
        return responseHandler.response(200, "Lesson updated successfully!", response);
    }

    // --- API: CẬP NHẬT TRẠNG THÁI BÀI HỌC (PATCH Status) ---
    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Cập nhật trạng thái của bài học (bao gồm vô hiệu hóa/kích hoạt)",
            description = "API này cho phép cập nhật trạng thái hoạt động của bài học thành 'ACTIVE' hoặc 'INACTIVE'."
    )
    @Parameter(
            name = "newStatus",
            description = "Trạng thái mới cho bài học. Giá trị hợp lệ: ACTIVE, INACTIVE",
            required = true,
            schema = @Schema(
                    type = "string",
                    allowableValues = {"ACTIVE", "INACTIVE"},
                    example = "ACTIVE"
            )
    )
    @ApiResponse(responseCode = "200", description = "Cập nhật trạng thái bài học thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LessonResponse.class)))
    @ApiResponse(responseCode = "400", description = "Trạng thái không hợp lệ.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "invalidStatus", summary = "Lỗi trạng thái không hợp lệ",
                    value = "{\n  \"statusCode\": 400,\n  \"message\": \"Invalid status value. Must be 'ACTIVE' or 'INACTIVE'.\",\n  \"details\": \"uri=/api/lessons/1/status\"\n}")))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy bài học.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "lessonNotFound", summary = "Lỗi không tìm thấy bài học",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Lesson not found with ID: 99\",\n  \"details\": \"uri=/api/lessons/99/status\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> changeLessonStatus(
            @PathVariable long id,
            @RequestParam String newStatus) {
        LessonResponse response = lessonService.changeLessonStatus(id, newStatus);
        return responseHandler.response(200, "Lesson status updated successfully!", response);
    }

    // --- API MỚI: LẤY DANH SÁCH BÀI HỌC THEO CHAPTER ID (GET By Chapter ID) ---
    @GetMapping("/by-chapter/{chapterId}")
    @Operation(
            summary = "Lấy danh sách các bài học theo ID chương",
            description = "API này trả về danh sách các bài học thuộc một chương cụ thể, có hỗ trợ phân trang, tìm kiếm và lọc."
    )
    @ApiResponse(responseCode = "200", description = "Lấy danh sách bài học theo chương thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy chương.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "chapterNotFound", summary = "Lỗi không tìm thấy chương",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Chapter not found with ID: 999\",\n  \"details\": \"uri=/api/lessons/by-chapter/999\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> getLessonsByChapterId(
            @PathVariable Long chapterId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @Parameter(
                    description = "Trạng thái của bài học để lọc. Giá trị hợp lệ: ACTIVE, INACTIVE",
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
        Page<LessonResponse> lessonPage = lessonService.getLessonsByChapterId(chapterId, pageForBackend, size, search, status, sortBy, sortDirection);
        return responseHandler.response(200, "Lessons retrieved successfully for Chapter ID: " + chapterId, lessonPage);
    }


}
