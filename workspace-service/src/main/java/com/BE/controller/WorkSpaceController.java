package com.BE.controller;

import com.BE.model.request.WorkSpaceRequest;
import com.BE.model.response.WorkSpaceResponse;
import com.BE.service.interfaceServices.IWorkSpaceService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(name = "WorkSpaces", description = "API quản lí Workspaces")
@RequestMapping("/api/workspaces")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class WorkSpaceController {
    @Autowired
    private IWorkSpaceService workSpaceService;

    @Autowired
    private ResponseHandler responseHandler;


    @Operation(summary = "Lấy danh sách tất cả workspace.", description = "Lấy danh sách tất cả workspace.")
    @ApiResponse(responseCode = "200", description = "Danh sách workspace.")
    @GetMapping
    public ResponseEntity getAll() {
        return responseHandler.response(200, "Lấy tất cả workspace thành công!", workSpaceService.getAll());
    }

    @Operation(summary = "Lấy thông tin workspace theo id.", description = "Lấy thông tin workspace theo id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thông tin workspace.", content = @Content(schema = @Schema(implementation = WorkSpaceResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy workspace.")
    })
    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable UUID id) {
        return responseHandler.response(200, "Lấy workspace theo id thành công!", workSpaceService.getById(id));
    }

    @Operation(summary = "Tạo mới workspace", description = "Tạo mới workspace cho user trong năm học nhất định.", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dữ liệu tạo workspace mới", required = true, content = @Content(schema = @Schema(implementation = WorkSpaceRequest.class), examples = @ExampleObject(value = "{\"name\":\"Workspace 2024\",\"academicYearId\":\"uuid-nam-hoc\",\"userId\":\"uuid-user\"}"))))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo thành công.", content = @Content(schema = @Schema(implementation = WorkSpaceResponse.class), examples = @ExampleObject(value = "{\"id\":\"uuid\",\"name\":\"Workspace 2024\",\"academicYearId\":\"uuid-nam-hoc\",\"userId\":\"uuid-user\"}"))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc thiếu trường bắt buộc."),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy user hoặc năm học.")
    })
    @PostMapping
    public ResponseEntity create(@Valid @RequestBody WorkSpaceRequest request) {
        return responseHandler.response(200, "Tạo workspace thành công!", workSpaceService.create(request));
    }

    @Operation(summary = "Cập nhật thông tin workspace", description = "Cập nhật thông tin workspace theo id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công.", content = @Content(schema = @Schema(implementation = WorkSpaceResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy workspace, user hoặc năm học.")
    })
    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable UUID id, @Valid @RequestBody WorkSpaceRequest request) {
        return responseHandler.response(200, "Cập nhật workspace thành công!", workSpaceService.update(id, request));
    }

    @Operation(summary = "Xóa workspace", description = "Xóa workspace theo id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xóa thành công."),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy workspace.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable UUID id) {
        workSpaceService.delete(id);
        return responseHandler.response(200, "Xóa workspace thành công!", 1);
    }

    @Operation(summary = "Lấy danh sách workspace có phân trang.", description = "Lấy danh sách workspace có phân trang.\n"
            +
            "- Chức năng: Trả về danh sách workspace theo từng trang, có thể truyền tham số page, size, sort.\n" +
            "- Điều kiện: Không cần điều kiện đặc biệt.\n" +
            "- Dữ liệu vào: page (số trang, bắt đầu từ 0), size (số lượng mỗi trang), sort (ví dụ: name,asc).\n" +
            "- Ví dụ: /api/workspaces/paged?page=0&size=5&sort=name,asc\n" +
            "- Đầu ra: Page<WorkSpaceResponse> (content, totalPages, totalElements, ...).\n" +
            "- Lỗi: Nếu truyền tham số không hợp lệ sẽ trả về lỗi 400.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách workspace phân trang thành công.", content = @Content(schema = @Schema(implementation = org.springframework.data.domain.Page.class), examples = @ExampleObject(value = "{\"content\":[{...}],\"totalPages\":2,\"totalElements\":10,\"size\":5,\"number\":0}"))),
            @ApiResponse(responseCode = "400", description = "Tham số phân trang không hợp lệ.")
    })
    @GetMapping("/paged")
    public ResponseEntity getAllPaged(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        return responseHandler.response(200, "Lấy danh sách workspace phân trang thành công!", workSpaceService.getAll(pageable));
    }

//    @Operation(summary = "Lấy danh sách workspace của account hiện tại trong năm học đang hoạt động.", description = "Lấy danh sách workspace của account hiện tại trong năm học đang hoạt động.")
//    @ApiResponse(responseCode = "200", description = "Danh sách workspace của account hiện tại trong năm học active.")
//    @GetMapping("/my")
//    public ResponseEntity getMyWorkspaces() {
//        var filtered = workSpaceService.getCurrentUserWorkspacesInActiveYear();
//        return responseHandler.response(200, "Lấy workspace của tài khoản hiện tại trong năm học đang hoạt động thành công!",
//                filtered);
//    }
}