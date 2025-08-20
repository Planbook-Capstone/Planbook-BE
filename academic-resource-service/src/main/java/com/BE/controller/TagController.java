package com.BE.controller;

import com.BE.model.request.TagCreateRequest;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.PagedResponse;
import com.BE.model.response.TagResponse;
import com.BE.service.implementServices.TagServiceImpl;
import com.BE.service.interfaceServices.TagService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@Tag(name = "Quản lý Loại học liệu", description = "APIs để quản lý loại học liệu")
public class TagController {

        private final TagService tagService;
        private final ResponseHandler responseHandler;

        @PostMapping
        @Operation(summary = "Tạo loại học liệu mới", description = "Tạo loại học liệu mới để phân loại tài nguyên")
        public ResponseEntity<DataResponseDTO<TagResponse>> createTag(
                        @Valid @RequestBody TagCreateRequest request) {

                TagResponse response = tagService.createTag(request);
                return responseHandler.response(201, "Loại học liệu được tạo thành công", response);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Lấy loại học liệu theo ID", description = "Lấy loại học liệu cụ thể theo ID")
        public ResponseEntity<DataResponseDTO<TagResponse>> getTagById(
                        @Parameter(description = "ID loại học liệu") @PathVariable Long id) {

                TagResponse response = tagService.getTagById(id);
                return responseHandler.response(200, "Loại học liệu được lấy thành công", response);
        }

        @GetMapping
        @Operation(summary = "Lấy tất cả loại học liệu", description = "Lấy tất cả loại học liệu được sắp xếp theo tên")
        public ResponseEntity<DataResponseDTO<List<TagResponse>>> getAllTags() {

                List<TagResponse> response = tagService.getAllTags();
                return responseHandler.response(200, "Loại học liệu được lấy thành công", response);
        }

        @GetMapping("/search")
        @Operation(summary = "Tìm kiếm loại học liệu", description = "Tìm kiếm loại học liệu theo từ khóa với phân trang")
        public ResponseEntity<DataResponseDTO<PagedResponse<TagResponse>>> searchTags(
                        @Parameter(description = "Từ khóa tìm kiếm") @RequestParam(required = false) String keyword,
                        @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") Integer page,
                        @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") Integer size) {

                PagedResponse<TagResponse> response = tagService.searchTags(keyword, page, size);
                return responseHandler.response(200, "Loại học liệu được lấy thành công", response);
        }

        @PutMapping("/{id}")
        @Operation(summary = "Cập nhật loại học liệu", description = "Cập nhật loại học liệu hiện có")
        public ResponseEntity<DataResponseDTO<TagResponse>> updateTag(
                        @Parameter(description = "ID loại học liệu") @PathVariable Long id,
                        @Valid @RequestBody TagCreateRequest request) {

                TagResponse response = tagService.updateTag(id, request);
                return responseHandler.response(200, "Loại học liệu được cập nhật thành công", response);
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Xóa loại học liệu", description = "Xóa loại học liệu (chỉ khi không được sử dụng bởi tài nguyên nào)")
        public ResponseEntity<DataResponseDTO<Void>> deleteTag(
                        @Parameter(description = "ID loại học liệu") @PathVariable Long id) {

                tagService.deleteTag(id);
                return responseHandler.response(200, "Loại học liệu được xóa thành công", null);
        }

        @GetMapping("/popular")
        @Operation(summary = "Lấy loại học liệu phổ biến", description = "Lấy loại học liệu được sắp xếp theo số lượng sử dụng")
        public ResponseEntity<DataResponseDTO<PagedResponse<Object[]>>> getPopularTags(
                        @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") Integer page,
                        @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") Integer size) {

                PagedResponse<Object[]> response = tagService.getPopularTags(page, size);
                return responseHandler.response(200, "Loại học liệu phổ biến được lấy thành công", response);
        }

        @GetMapping("/resource/{resourceId}")
        @Operation(summary = "Lấy loại học liệu theo tài nguyên", description = "Lấy tất cả loại học liệu liên quan đến tài nguyên cụ thể")
        public ResponseEntity<DataResponseDTO<List<TagResponse>>> getTagsByResourceId(
                        @Parameter(description = "ID tài nguyên") @PathVariable Long resourceId) {

                List<TagResponse> response = tagService.getTagsByResourceId(resourceId);
                return responseHandler.response(200, "Loại học liệu của tài nguyên được lấy thành công", response);
        }

        @GetMapping("/unused")
        @Operation(summary = "Lấy loại học liệu chưa sử dụng", description = "Lấy loại học liệu chưa được liên kết với tài nguyên nào")
        public ResponseEntity<DataResponseDTO<List<TagResponse>>> getUnusedTags() {

                List<TagResponse> response = tagService.getUnusedTags();
                return responseHandler.response(200, "Loại học liệu chưa sử dụng được lấy thành công", response);
        }
}
