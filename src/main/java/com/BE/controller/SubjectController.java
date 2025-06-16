package com.BE.controller;

import com.BE.model.request.SubjectRequest;
import com.BE.model.response.SubjectResponse;
import com.BE.service.interfaceServices.ISubjectService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Subjects", description = "API quản lí môn học")
@RequestMapping("/api/subjects")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubjectController {


    ISubjectService subjectService;
    ResponseHandler responseHandler;



    @PostMapping
    @Operation(
            summary = "Tạo một môn học mới",
            description = "API này cho phép tạo một môn học mới với tên và ID khối lớp liên kết. Tên môn học phải là duy nhất trong phạm vi một khối lớp."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody( // Đây là annotation của Swagger để mô tả request body
            description = "Thông tin chi tiết của môn học cần tạo. " +
                    "Dữ liệu yêu cầu phải là JSON với trường 'name' không được để trống và 'gradeId' không được null.",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SubjectRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "createSubjectChemistryExample",
                                    summary = "Ví dụ tạo môn Hóa học (mặc định) cho Lớp 10",
                                    value = "{\"name\": \"Hóa học 10\", \"gradeId\": 1}"
                            ),
                            @ExampleObject(
                                    name = "createSubjectMathExample",
                                    summary = "Ví dụ tạo môn Toán học cho Lớp 10",
                                    value = "{\"name\": \"Toán học 10\", \"gradeId\": 1}" // Giả sử Grade ID 1 là "Lớp 10"
                            ),
                            @ExampleObject(
                                    name = "createSubjectPhysicsExample",
                                    summary = "Ví dụ tạo môn Vật lý cho Lớp 10",
                                    value = "{\"name\": \"Vật lý 10\", \"gradeId\": 1}"
                            ),
                            @ExampleObject(
                                    name = "createSubjectBiologyExample",
                                    summary = "Ví dụ tạo môn Sinh học cho Lớp 10",
                                    value = "{\"name\": \"Sinh học 10\", \"gradeId\": 1}" // Giả sử Grade ID 2 là "Lớp 11"
                            ),
                            @ExampleObject(
                                    name = "createSubjectLiteratureExample",
                                    summary = "Ví dụ tạo môn Ngữ văn cho Lớp 10",
                                    value = "{\"name\": \"Ngữ văn 10\", \"gradeId\": 1}" // Giả sử Grade ID 3 là "Lớp 12"
                            ),
                            @ExampleObject(
                                    name = "createSubjectEnglishExample",
                                    summary = "Ví dụ tạo môn Tiếng Anh cho Lớp 10",
                                    value = "{\"name\": \"Tiếng Anh 10\", \"gradeId\": 1}"
                            )
                    }
            )
    )
    @ApiResponse(responseCode = "201", description = "Môn học được tạo thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubjectResponse.class),
                    examples = @ExampleObject(name = "subjectCreationSuccessResponse", summary = "Phản hồi tạo môn học thành công",
                            value = "{\n  \"statusCode\": 201,\n  \"message\": \"Subject created successfully!\",\n  \"data\": {\n    \"id\": 1,\n    \"name\": \"Toán học\",\n    \"status\": \"ACTIVE\",\n    \"createdAt\": \"2025-06-08T10:00:00Z\",\n    \"updatedAt\": \"2025-06-08T10:00:00Z\",\n    \"grade\": {\n      \"id\": 101,\n      \"name\": \"Lớp 10\",\n      \"status\": \"ACTIVE\",\n      \"createdAt\": \"2025-06-07T09:00:00Z\",\n      \"updatedAt\": \"2025-06-07T09:00:00Z\"\n    }\n  }\n}")))
    @ApiResponse(responseCode = "400", description = "Dữ liệu yêu cầu không hợp lệ hoặc tên môn học đã tồn tại trong khối lớp.",
            content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "invalidInput", summary = "Lỗi dữ liệu đầu vào không hợp lệ",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"Subject name cannot be blank\",\n  \"details\": \"uri=/api/subjects\"\n}"),
                    @ExampleObject(name = "duplicateSubjectName", summary = "Lỗi tên môn học đã tồn tại",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"Subject with name 'Toán học' already exists for Grade ID: 101\",\n  \"details\": \"uri=/api/subjects\"\n}")
            }))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy khối lớp.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "gradeNotFound", summary = "Lỗi không tìm thấy khối lớp",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Grade not found with ID: 999\",\n  \"details\": \"uri=/api/subjects\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> createSubject(@Valid @RequestBody SubjectRequest request) {
        SubjectResponse response = subjectService.createSubject(request);
        return responseHandler.response(201, "Subject created successfully!", response);
    }

    // --- API: LẤY DANH SÁCH MÔN HỌC (GET All Subjects) ---
    @GetMapping
    @Operation(
            summary = "Lấy danh sách các môn học",
            description = "API này hỗ trợ phân trang, tìm kiếm theo tên, lọc theo trạng thái và sắp xếp. " +
                    "Nếu các tham số tìm kiếm/lọc rỗng, nó sẽ trả về tất cả các môn học." +
                    "Số trang bắt đầu từ 1 (page=1), kích thước trang mặc định là 10 (size=10)."
    )
    @ApiResponse(responseCode = "200", description = "Lấy danh sách môn học thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> getAllSubjects(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @Parameter(
                    description = "Trạng thái của môn học để lọc. Giá trị hợp lệ: ACTIVE, INACTIVE",
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
        Page<SubjectResponse> subjectPage = subjectService.getAllSubjects(pageForBackend, size, search, status, sortBy, sortDirection);
        return responseHandler.response(200, "Subjects retrieved successfully!", subjectPage);
    }

    // --- API: LẤY MÔN HỌC BẰNG ID (GET By ID) ---
    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin một môn học theo ID",
            description = "API này trả về thông tin chi tiết của một môn học dựa trên ID duy nhất của nó."
    )
    @ApiResponse(responseCode = "200", description = "Lấy thông tin môn học thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubjectResponse.class),
                    examples = @ExampleObject(name = "subjectByIdSuccessResponse", summary = "Phản hồi lấy môn học theo ID thành công",
                            value = "{\n  \"statusCode\": 200,\n  \"message\": \"Subject retrieved successfully!\",\n  \"data\": {\n    \"id\": 1,\n    \"name\": \"Toán học\",\n    \"status\": \"ACTIVE\",\n    \"createdAt\": \"2025-06-08T10:00:00Z\",\n    \"updatedAt\": \"2025-06-08T10:00:00Z\",\n    \"grade\": {\n      \"id\": 101,\n      \"name\": \"Lớp 10\",\n      \"status\": \"ACTIVE\",\n      \"createdAt\": \"2025-06-07T09:00:00Z\",\n      \"updatedAt\": \"2025-06-07T09:00:00Z\"\n    }\n  }\n}")))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy môn học.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "subjectNotFound", summary = "Lỗi không tìm thấy môn học",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Subject not found with ID: 99\",\n  \"details\": \"uri=/api/subjects/99\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> getSubjectById(@PathVariable long id) {
        SubjectResponse response = subjectService.getSubjectById(id);
        return responseHandler.response(200, "Subject retrieved successfully!", response);
    }

    // --- API: CẬP NHẬT MÔN HỌC (PUT) ---
    @PutMapping("/{id}")
    @Operation(
            summary = "Cập nhật thông tin một môn học",
            description = "API này cho phép cập nhật tên và ID khối lớp của một môn học. Tên môn học phải là duy nhất trong phạm vi một khối lớp."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody( // Thêm RequestBody cho Update Subject
            description = "Thông tin chi tiết của môn học cần cập nhật. " +
                    "Dữ liệu yêu cầu phải là JSON với trường 'name' không được để trống và 'gradeId' không được null.",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SubjectRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "updateSubjectChemistryExample",
                                    summary = "Ví dụ cập nhật môn Hóa học 10",
                                    value = "{\"name\": \"Hóa học 10 Cập nhật\", \"gradeId\": 1}" // Cập nhật tên
                            ),
                            @ExampleObject(
                                    name = "updateSubjectMathToGrade11Example",
                                    summary = "Ví dụ cập nhật môn Toán học 10",
                                    value = "{\"name\": \"Toán học 10 Cập nhật\", \"gradeId\": 2}" // Giả sử Grade ID 2 là "Lớp 11"
                            ),
                            @ExampleObject(
                                    name = "updateSubjectBiologyAndGradeExample",
                                    summary = "Ví dụ cập nhật tên và di chuyển môn Sinh học",
                                    value = "{\"name\": \"Sinh học Nâng cao\", \"gradeId\": 1}"
                            )
                    }
            )
    )
    @ApiResponse(responseCode = "200", description = "Môn học được cập nhật thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubjectResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dữ liệu yêu cầu không hợp lệ hoặc tên môn học đã tồn tại trong khối lớp.",
            content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "invalidInput", summary = "Lỗi dữ liệu đầu vào không hợp lệ",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"Subject name cannot be blank\",\n  \"details\": \"uri=/api/subjects/1\"\n}"),
                    @ExampleObject(name = "duplicateSubjectName", summary = "Lỗi tên môn học đã tồn tại",
                            value = "{\n  \"statusCode\": 400,\n  \"message\": \"Subject with name 'Toán học' already exists for Grade ID: 101\",\n  \"details\": \"uri=/api/subjects/1\"\n}")
            }))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy môn học hoặc khối lớp mới.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "subjectOrGradeNotFound", summary = "Lỗi không tìm thấy môn học hoặc khối lớp",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Subject not found with ID: 99\",\n  \"details\": \"uri=/api/subjects/99\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> updateSubject(@PathVariable long id, @Valid @RequestBody SubjectRequest request) {
        SubjectResponse response = subjectService.updateSubject(id, request);
        return responseHandler.response(200, "Subject updated successfully!", response);
    }

    // --- API: CẬP NHẬT TRẠNG THÁI MÔN HỌC (PATCH Status) ---
    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Cập nhật trạng thái của môn học (bao gồm vô hiệu hóa/kích hoạt)",
            description = "API này cho phép cập nhật trạng thái hoạt động của môn học thành 'ACTIVE' hoặc 'INACTIVE'. " +
                    "Sử dụng query parameter 'newStatus' để truyền trạng thái mới."
    )
    @Parameter(
            name = "newStatus",
            description = "Trạng thái mới cho môn học. Giá trị hợp lệ: ACTIVE, INACTIVE",
            required = true,
            schema = @Schema(
                    type = "string",
                    allowableValues = {"ACTIVE", "INACTIVE"},
                    example = "ACTIVE"
            )
    )
    @ApiResponse(responseCode = "200", description = "Cập nhật trạng thái môn học thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubjectResponse.class)))
    @ApiResponse(responseCode = "400", description = "Trạng thái không hợp lệ.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "invalidStatus", summary = "Lỗi trạng thái không hợp lệ",
                    value = "{\n  \"statusCode\": 400,\n  \"message\": \"Invalid status value. Must be 'ACTIVE' or 'INACTIVE'.\",\n  \"details\": \"uri=/api/subjects/1/status\"\n}")))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy môn học.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "subjectNotFound", summary = "Lỗi không tìm thấy môn học",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Subject not found with ID: 99\",\n  \"details\": \"uri=/api/subjects/99/status\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> changeSubjectStatus(
            @PathVariable long id,
            @RequestParam String newStatus) {
        SubjectResponse response = subjectService.changeSubjectStatus(id, newStatus);
        return responseHandler.response(200, "Subject status updated successfully!", response);
    }

    // --- API MỚI: LẤY DANH SÁCH MÔN HỌC THEO GRADE ID (GET By Grade ID) ---
    @GetMapping("/by-grade/{gradeId}")
    @Operation(
            summary = "Lấy danh sách các môn học theo ID khối lớp",
            description = "API này trả về danh sách các môn học thuộc một khối lớp cụ thể, có hỗ trợ phân trang, tìm kiếm và lọc." +
                    "Số trang bắt đầu từ 1 (page=1), kích thước trang mặc định là 10 (size=10)."
    )
    @ApiResponse(responseCode = "200", description = "Lấy danh sách môn học theo khối lớp thành công.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy khối lớp.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "gradeNotFound", summary = "Lỗi không tìm thấy khối lớp",
                    value = "{\n  \"statusCode\": 404,\n  \"message\": \"Grade not found with ID: 999\",\n  \"details\": \"uri=/api/subjects/by-grade/999\"\n}")))
    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ.")
    public ResponseEntity<Object> getSubjectsByGradeId(
            @PathVariable Long gradeId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @Parameter(
                    description = "Trạng thái của môn học để lọc. Giá trị hợp lệ: ACTIVE, INACTIVE",
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
        Page<SubjectResponse> subjectPage = subjectService.getSubjectsByGradeId(gradeId, pageForBackend, size, search, status, sortBy, sortDirection);
        return responseHandler.response(200, "Subjects retrieved successfully for Grade ID: " + gradeId, subjectPage);
    }

}
