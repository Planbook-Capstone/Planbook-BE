package com.BE.controller;

import com.BE.enums.AcademicResourceEnum;
import com.BE.enums.SortBy;
import com.BE.enums.SortDirection;
import com.BE.model.request.AcademicResourceCreateRequest;
import com.BE.model.request.AcademicResourceCreateWithFileRequest;
import com.BE.model.request.AcademicResourceSearchRequest;
import com.BE.model.request.AcademicResourceUpdateRequest;
import com.BE.model.response.*;
import com.BE.service.implementServices.AcademicResourceServiceImpl;
import com.BE.service.implementServices.SupabaseStorageServiceImpl;
import com.BE.service.interfaceServices.AcademicResourceService;
import com.BE.service.interfaceServices.SupabaseStorageService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/academic-resources")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@Tag(name = "Quản lý Tài nguyên Học thuật", description = "APIs để quản lý tài nguyên học thuật")
public class AcademicResourceController {

        private final AcademicResourceService academicResourceService;
        private final SupabaseStorageService supabaseStorageService;
        private final ResponseHandler responseHandler;

        @PostMapping
        @Operation(summary = "Tạo tài nguyên học thuật mới", description = "Tạo tài nguyên học thuật mới với metadata")
        public ResponseEntity<DataResponseDTO<AcademicResourceResponse>> createResource(
                        @Valid @RequestBody AcademicResourceCreateRequest request) {

                AcademicResourceResponse response = academicResourceService.createResource(request);
                return responseHandler.response(201, "Tài nguyên học thuật được tạo thành công", response);
        }

        @PostMapping(value = "/internal", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @Operation(summary = "Tạo tài nguyên học thuật nội bộ với tải tệp", description = "Tạo tài nguyên học thuật nội bộ mới và tải tệp lên Supabase storage")
        public ResponseEntity<DataResponseDTO<AcademicResourceInternalResponse>> createResourceInternal(
                        @Parameter(description = "Tệp cần tải lên") @RequestParam("file") MultipartFile file) {
                AcademicResourceInternalResponse response = academicResourceService.createResourceInternal(file);
                return responseHandler.response(201, "Tài nguyên học thuật nội bộ được tạo với tải tệp thành công",
                                response);
        }

        @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @Operation(summary = "Tạo tài nguyên học thuật với tải tệp", description = "Tạo tài nguyên học thuật mới và tải tệp lên Supabase storage")
        public ResponseEntity<Object> createResourceWithFile(
                        @ModelAttribute @Valid AcademicResourceCreateWithFileRequest request) {
                AcademicResourceResponse response = academicResourceService.createResourceWithFile(request);
                return responseHandler.response(201, "Tài nguyên học thuật được tạo với tải tệp thành công",
                                response);
        }

        @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @Operation(summary = "Chỉ tải tệp lên", description = "Tải tệp lên Supabase storage và lấy URL")
        public ResponseEntity<Object> uploadFile(
                        @Parameter(description = "Tệp cần tải lên") @RequestParam("file") MultipartFile file)
                        throws IOException {

                FileUploadResponse response = supabaseStorageService.uploadFile(file);
                return responseHandler.response(200, "Tệp được tải lên thành công", response);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Lấy tài nguyên học thuật theo ID", description = "Lấy tài nguyên học thuật cụ thể theo ID")
        public ResponseEntity<Object> getResourceById(
                        @Parameter(description = "ID tài nguyên") @PathVariable Long id) {

                AcademicResourceResponse response = academicResourceService.getResourceById(id);
                return responseHandler.response(200, "Tài nguyên học thuật được lấy thành công", response);
        }

        @GetMapping("/internal")
        @Operation(summary = "Lấy tài nguyên học thuật nội bộ theo ID người tạo", description = "Lấy tài nguyên học thuật nội bộ theo ID người tạo")
        public ResponseEntity<Object> getInternalResourceByCreatorId(
                        @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") Integer page,
                        @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") Integer size) {
                PagedResponse<AcademicResourceInternalResponse> response = academicResourceService.getResourcesByCreatorId( page, size);
                return responseHandler.response(200, "Tài nguyên học thuật nội bộ được lấy thành công", response);
        }

        @PutMapping("/{id}")
        @Operation(summary = "Cập nhật tài nguyên học thuật", description = "Cập nhật tài nguyên học thuật hiện có")
        public ResponseEntity<Object> updateResource(
                        @Parameter(description = "ID tài nguyên") @PathVariable Long id,
                        @Valid @RequestBody AcademicResourceUpdateRequest request) {

                AcademicResourceResponse response = academicResourceService.updateResource(id, request);
                return responseHandler.response(200, "Tài nguyên học thuật được cập nhật thành công", response);
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Xóa tài nguyên học thuật", description = "Xóa tài nguyên học thuật và tệp liên quan")
        public ResponseEntity<Object> deleteResource(
                        @Parameter(description = "ID tài nguyên") @PathVariable Long id) {

                academicResourceService.deleteResource(id);
                return responseHandler.response(200, "Tài nguyên học thuật được xóa thành công", null);
        }

        @GetMapping("/search")
        @Operation(summary = "Tìm kiếm tài nguyên học thuật", description = "Tìm kiếm và lọc tài nguyên học thuật với phân trang")
        public ResponseEntity<Object> searchResources(
                @Parameter(description = "ID của giáo viên tạo") @RequestParam(required = false) UUID createdBy,
                @Parameter(description = "Từ khóa tìm kiếm") @RequestParam(required = false) String keyword,
                @Parameter(description = "Loại tài nguyên") @RequestParam(required = false) String type,
                @Parameter(description = "Danh sách tagId") @RequestParam(required = false) Set<Long> tagIds,
                @Parameter(description = "Số trang (bắt đầu từ 1)") @RequestParam(defaultValue = "1") Integer page,
                @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") Integer size,
                @Parameter(description = "Trường sắp xếp", schema = @Schema(implementation = SortBy.class))
                @RequestParam(defaultValue = "CREATED_AT") SortBy sortBy,
                @Parameter(description = "Hướng sắp xếp", schema = @Schema(implementation = SortDirection.class))
                @RequestParam(defaultValue = "DESC") SortDirection sortDirection,
                @Parameter(description = "Phạm vi hiển thị", schema = @Schema(implementation = AcademicResourceEnum.class))
                @RequestParam(required = false) AcademicResourceEnum visibility
        ) {
                AcademicResourceSearchRequest searchRequest = new AcademicResourceSearchRequest();
                searchRequest.setCreatedBy(createdBy);
                searchRequest.setKeyword(keyword);
                searchRequest.setType(type);
                searchRequest.setTagIds(tagIds);
                searchRequest.setPage(page);
                searchRequest.setSize(size);
                searchRequest.setSortBy(sortBy);
                searchRequest.setSortDirection(sortDirection);
                searchRequest.setVisibility(visibility);

                PagedResponse<AcademicResourceResponse> response = academicResourceService.searchResources(searchRequest);
                return responseHandler.response(200, "Tài nguyên học thuật được lấy thành công", response);
        }


}
