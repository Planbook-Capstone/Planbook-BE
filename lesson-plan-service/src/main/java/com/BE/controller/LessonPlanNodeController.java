package com.BE.controller;

import com.BE.model.request.CreateLessonPlanNodeRequest;
import com.BE.model.request.UpdateLessonPlanNodeRequest;
import com.BE.model.response.LessonPlanNodeDTO;
import com.BE.service.interfaceServices.LessonPlanNodeService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
@SecurityRequirement(name = "api")
@Tag(name = "Lesson Plan Node Controller", description = "Quản lý cấu trúc cây giáo án")
public class LessonPlanNodeController {

    @Autowired
    LessonPlanNodeService lessonPlanNodeService;

    @Autowired
    ResponseHandler responseHandler;

    @GetMapping("/lesson-nodes/{lessonPlanTemplateId}/tree")
    @Operation(summary = "Lấy các root nodes của giáo án",
               description = "Trả về các root nodes (các bước chính) của giáo án, không bao gồm children")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy root nodes thành công",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = LessonPlanNodeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy giáo án"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    public ResponseEntity getLessonTree(@PathVariable Long lessonPlanTemplateId) {
        List<LessonPlanNodeDTO> rootNodes = lessonPlanNodeService.getLessonTree(lessonPlanTemplateId);
        return responseHandler.response(200, "Lấy root nodes thành công!", rootNodes);
    }

    @GetMapping("/lesson-nodes/{nodeId}/children")
    @Operation(summary = "Lấy tất cả children của một node",
               description = "Trả về tất cả children của một node với cấu trúc phân cấp đầy đủ đến tận rễ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy children thành công",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = LessonPlanNodeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy node"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    public ResponseEntity getNodeChildren(@PathVariable Long nodeId) {
        List<LessonPlanNodeDTO> children = lessonPlanNodeService.getNodeChildren(nodeId);
        return responseHandler.response(200, "Lấy children thành công!", children);
    }

    @PostMapping("/lesson-nodes")
    @Operation(summary = "Tạo node mới trong cây giáo án", 
               description = "Thêm một node mới vào giáo án (section, subsection, list item, paragraph)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo node thành công",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = LessonPlanNodeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Thông tin node mới cần tạo trong cây giáo án",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CreateLessonPlanNodeRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "Tạo Section",
                                    summary = "Tạo một section chính",
                                    value = "{\"lessonPlanId\": 101, \"parentId\": null, \"title\": \"Mục tiêu bài học\", \"content\": \"Học sinh hiểu được khái niệm cơ bản\", \"description\": \"Học sinh hiểu được khái niệm cơ bản\", \"fieldType\": \"INPUT\", \"type\": \"SECTION\", \"orderIndex\": 1}"
                            ),
                            @ExampleObject(
                                    name = "Tạo Subsection",
                                    summary = "Tạo một subsection con",
                                    value = "{\"lessonPlanId\": 101, \"parentId\": 1, \"title\": \"Kiến thức cần đạt\", \"content\": \"Nắm vững lý thuyết\", \"description\": \"Học sinh hiểu được khái niệm cơ bản\", \"fieldType\": \"UPLOAD\", \"type\": \"SUBSECTION\", \"orderIndex\": 1}"
                            ),
                            @ExampleObject(
                                    name = "Tạo List Item",
                                    summary = "Tạo một mục danh sách",
                                    value = "{\"lessonPlanId\": 101, \"parentId\": 2, \"title\": \"Điểm 1\", \"content\": \"Hiểu khái niệm A\", \"description\": \"Học sinh hiểu được khái niệm cơ bản\", \"fieldType\": \"SELECT\", \"type\": \"LIST_ITEM\", \"orderIndex\": 1}"
                            )
                    }
            )
    )
    public ResponseEntity createNode(@Valid @RequestBody CreateLessonPlanNodeRequest request) {
        LessonPlanNodeDTO createdNode = lessonPlanNodeService.createNode(request);
        return responseHandler.response(200, "Tạo node giáo án thành công!", createdNode);
    }

    @PutMapping("/lesson-nodes/{id}")
    @Operation(summary = "Cập nhật nội dung node trong cây giáo án", 
               description = "Cập nhật nội dung, thứ tự hoặc metadata của node trong cây giáo án")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật node thành công",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = LessonPlanNodeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy node"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Thông tin cập nhật cho node trong cây giáo án",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UpdateLessonPlanNodeRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "Cập nhật tiêu đề và nội dung",
                                    summary = "Cập nhật tiêu đề và nội dung của node",
                                    value = "{\"title\": \"Mục tiêu bài học đã cập nhật\", \"content\": \"Học sinh hiểu được khái niệm nâng cao\", \"description\": \"Học sinh hiểu được khái niệm cơ bản\", \"fieldType\": \"INPUT\", \"type\": \"SUBSECTION\", \"orderIndex\": 2}"
                            ),
                            @ExampleObject(
                                    name = "Cập nhật thứ tự",
                                    summary = "Thay đổi thứ tự sắp xếp của node",
                                    value = "{\"orderIndex\": 3}"
                            ),
                            @ExampleObject(
                                    name = "Cập nhật loại node",
                                    summary = "Thay đổi loại của node",
                                    value = "{\"type\": \"PARAGRAPH\", \"title\": \"Đoạn văn mô tả\"}"
                            )
                    }
            )
    )
    public ResponseEntity updateNode(@PathVariable Long id, @Valid @RequestBody UpdateLessonPlanNodeRequest request) {
        LessonPlanNodeDTO updatedNode = lessonPlanNodeService.updateNode(id, request);
        return responseHandler.response(200, "Cập nhật node giáo án thành công!", updatedNode);
    }

    @DeleteMapping("/lesson-nodes/{id}")
    @Operation(summary = "Xoá node khỏi cây giáo án",
               description = "Xoá một node và toàn bộ các node con của nó khỏi cây giáo án")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xoá node thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy node"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    public ResponseEntity deleteNode(@PathVariable Long id) {
        lessonPlanNodeService.deleteNode(id);
        return responseHandler.response(200, "Xoá node giáo án thành công!", null);
    }

    @GetMapping("/admin/lesson-nodes/{lessonPlanTemplateId}/all-nodes")
    @Operation(
            summary = "Lấy tất cả nodes của giáo án (bao gồm cả inactive) - Admin only",
            description = "Trả về tất cả nodes của giáo án với cấu trúc phân cấp đầy đủ, bao gồm cả các node đã bị xóa (inactive). Chỉ dành cho admin."
    )
    @SecurityRequirement(name = "api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy tất cả nodes thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy giáo án"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    public ResponseEntity<Object> getAllNodesByLessonPlanId(
            @io.swagger.v3.oas.annotations.Parameter(description = "ID của giáo án", example = "101")
            @PathVariable Long lessonPlanTemplateId) {

        List<LessonPlanNodeDTO> nodes = lessonPlanNodeService.getAllNodesByLessonPlanTemplateId(lessonPlanTemplateId);
        return responseHandler.response(200, "Lấy tất cả nodes thành công!", nodes);
    }
}
