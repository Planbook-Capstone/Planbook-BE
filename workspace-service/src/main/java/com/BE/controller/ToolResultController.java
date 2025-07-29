package com.BE.controller;

import com.BE.model.request.CreateToolResultRequest;
import com.BE.model.request.ToolResultFilterRequest;
import com.BE.model.request.UpdateToolResultRequest;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.ToolResultResponse;
import com.BE.service.interfaceServices.IToolResultService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller cho quản lý ToolResult
 */
@RestController
@Tag(name = "Tool Results", description = "API quản lý kết quả công cụ AI")
@RequestMapping("/api/tool-results")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
public class ToolResultController {

    private final IToolResultService toolResultService;
    private final ResponseHandler<Object> responseHandler;

    @Operation(
            summary = "Tạo mới kết quả công cụ AI",
            description = "Tạo mới một kết quả từ việc sử dụng công cụ AI như tạo giáo án, slide, đề kiểm tra, v.v.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dữ liệu tạo mới ToolResult",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateToolResultRequest.class),
                            examples = @ExampleObject(
                                    name = "Ví dụ tạo giáo án",
                                    value = """
                                            {
                                              "userId": "0d29b45a-5d6a-44e2-b58d-d7aa5180cb0f",
                                              "workspaceId": 101,
                                              "type": "LESSON_PLAN",
                                              "templateId": 5,
                                              "name": "Giáo án bài 3",
                                              "description": "Giáo án được tạo từ AI",
                                              "data": {
                                                "title": "Bài học về nguyên tố hóa học",
                                                "objectives": ["Hiểu khái niệm nguyên tố", "Tính nguyên tử khối"]
                                              },
                                              "status": "DRAFT"
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tạo thành công",
                    content = @Content(
                            schema = @Schema(implementation = ToolResultResponse.class),
                            examples = @ExampleObject(
                                    name = "Response thành công",
                                    value = """
                                            {
                                              "statusCode": 200,
                                              "message": "Tạo kết quả công cụ AI thành công!",
                                              "data": {
                                                "id": 123,
                                                "userId": "0d29b45a-5d6a-44e2-b58d-d7aa5180cb0f",
                                                "workspaceId": 101,
                                                "type": "LESSON_PLAN",
                                                "templateId": 5,
                                                "name": "Giáo án bài 3",
                                                "description": "Giáo án được tạo từ AI",
                                                "data": {
                                                  "title": "Bài học về nguyên tố hóa học",
                                                  "objectives": ["Hiểu khái niệm nguyên tố", "Tính nguyên tử khối"]
                                                },
                                                "status": "DRAFT",
                                                "createdAt": "2025-07-27T12:34:56",
                                                "updatedAt": "2025-07-27T12:34:56"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc thiếu trường bắt buộc"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateToolResultRequest request) {
        ToolResultResponse result = toolResultService.create(request);
        return responseHandler.response(200, "Tạo kết quả công cụ AI thành công!", result);
    }

    @Operation(
            summary = "Cập nhật kết quả công cụ AI",
            description = "Cập nhật thông tin của một kết quả công cụ AI theo ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", content = @Content(schema = @Schema(implementation = ToolResultResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy ToolResult với ID đã cho"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @Parameter(description = "ID của ToolResult cần cập nhật", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateToolResultRequest request
    ) {
        ToolResultResponse result = toolResultService.update(id, request);
        return responseHandler.response(200, "Cập nhật kết quả công cụ AI thành công!", result);
    }

    @Operation(
            summary = "Lấy chi tiết kết quả công cụ AI",
            description = "Lấy thông tin chi tiết của một kết quả công cụ AI theo ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy thông tin thành công", content = @Content(schema = @Schema(implementation = ToolResultResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy ToolResult với ID đã cho")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @Parameter(description = "ID của ToolResult cần lấy", required = true)
            @PathVariable Long id
    ) {
        ToolResultResponse result = toolResultService.getById(id);
        return responseHandler.response(200, "Lấy chi tiết kết quả công cụ AI thành công!", result);
    }

    @Operation(
            summary = "Lấy danh sách kết quả công cụ AI với filter linh hoạt và phân trang",
            description = """
                    Lấy danh sách kết quả công cụ AI với các bộ lọc linh hoạt:
                    - userIds: Danh sách ID người dùng (có thể multiple)
                    - workspaceIds: Danh sách ID workspace (có thể multiple)
                    - types: Danh sách loại công cụ (có thể multiple)
                    - statuses: Danh sách trạng thái (có thể multiple)
                    - templateIds: Danh sách ID template (có thể multiple)
                    - nameContains: Tìm kiếm theo tên (contains, case insensitive)
                    - descriptionContains: Tìm kiếm theo mô tả (contains, case insensitive)
                    - createdAfter/createdBefore: Filter theo khoảng thời gian tạo
                    - updatedAfter/updatedBefore: Filter theo khoảng thời gian cập nhật
                    - page: Số trang (bắt đầu từ 1)
                    - size: Kích thước trang
                    - sortBy: Trường sắp xếp
                    - sortDirection: Hướng sắp xếp (asc/desc)

                    Ví dụ: /api/tool-results?userIds=uuid1,uuid2&workspaceIds=101,102&types=LESSON_PLAN,SLIDE&statuses=DRAFT,PUBLISHED&page=1&size=10&sortBy=createdAt&sortDirection=desc
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataResponseDTO.class),
                            examples = @ExampleObject(name = "Response mẫu", value = """
                                    {
                                      "statusCode": 200,
                                      "message": "Lấy danh sách kết quả công cụ AI thành công!",
                                      "data": {
                                        "content": [
                                          {
                                            "id": 123,
                                            "userId": "0d29b45a-5d6a-44e2-b58d-d7aa5180cb0f",
                                            "workspaceId": 101,
                                            "type": "LESSON_PLAN",
                                            "name": "Giáo án bài 3",
                                            "description": "Giáo án thử nghiệm với chương 2",
                                            "lessonIds": [456, 457],
                                            "status": "DRAFT",
                                            "createdAt": "2025-07-27T12:34:56",
                                            "updatedAt": "2025-07-27T12:34:56"
                                          }
                                        ],
                                        "totalPages": 1,
                                        "totalElements": 1,
                                        "size": 10,
                                        "number": 0,
                                        "first": true,
                                        "last": true
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Tham số không hợp lệ")
    })
    @GetMapping
    public ResponseEntity<?> getAll(ToolResultFilterRequest filterRequest) {
        var result = toolResultService.getAllWithFilter(filterRequest);
        return responseHandler.response(200, "Lấy danh sách kết quả công cụ AI thành công!", result);
    }

    @Operation(
            summary = "Xóa kết quả công cụ AI",
            description = "Xóa một kết quả công cụ AI theo ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy ToolResult với ID đã cho"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @Parameter(description = "ID của ToolResult cần xóa", required = true)
            @PathVariable Long id
    ) {
        toolResultService.delete(id);
        return responseHandler.response(200, "Xóa kết quả công cụ AI thành công!", null);
    }


}
