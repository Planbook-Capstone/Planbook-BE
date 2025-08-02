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
@Tag(name = "Academic Resource Management", description = "APIs for managing academic resources")
public class AcademicResourceController {

        private final AcademicResourceService academicResourceService;
        private final SupabaseStorageService supabaseStorageService;
        private final ResponseHandler responseHandler;

        @PostMapping
        @Operation(summary = "Create a new academic resource", description = "Create a new academic resource with metadata")
        public ResponseEntity<DataResponseDTO<AcademicResourceResponse>> createResource(
                        @Valid @RequestBody AcademicResourceCreateRequest request) {

                AcademicResourceResponse response = academicResourceService.createResource(request);
                return responseHandler.response(201, "Academic resource created successfully", response);
        }

        @PostMapping(value = "/internal", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @Operation(summary = "Create Internal academic resource with file upload", description = "Create a new Internal academic resource and upload file to Supabase storage")
        public ResponseEntity<DataResponseDTO<AcademicResourceInternalResponse>> createResourceInternal(
                        @Parameter(description = "File to upload") @RequestParam("file") MultipartFile file) {
                AcademicResourceInternalResponse response = academicResourceService.createResourceInternal(file);
                return responseHandler.response(201, "Internal Academic resource created with file upload successfully",
                                response);
        }

        @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @Operation(summary = "Create academic resource with file upload", description = "Create a new academic resource and upload file to Supabase storage")
        public ResponseEntity<Object> createResourceWithFile(
                        @ModelAttribute @Valid AcademicResourceCreateWithFileRequest request) {
                AcademicResourceResponse response = academicResourceService.createResourceWithFile(request);
                return responseHandler.response(201, "Academic resource created with file upload successfully",
                                response);
        }

        @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @Operation(summary = "Upload file only", description = "Upload a file to Supabase storage and get the URL")
        public ResponseEntity<Object> uploadFile(
                        @Parameter(description = "File to upload") @RequestParam("file") MultipartFile file)
                        throws IOException {

                FileUploadResponse response = supabaseStorageService.uploadFile(file);
                return responseHandler.response(200, "File uploaded successfully", response);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get academic resource by ID", description = "Retrieve a specific academic resource by its ID")
        public ResponseEntity<Object> getResourceById(
                        @Parameter(description = "Resource ID") @PathVariable Long id) {

                AcademicResourceResponse response = academicResourceService.getResourceById(id);
                return responseHandler.response(200, "Academic resource retrieved successfully", response);
        }

        @GetMapping("/internal")
        @Operation(summary = "Get internal academic resource by creator ID", description = "Retrieve an internal academic resource by creator ID")
        public ResponseEntity<Object> getInternalResourceByCreatorId(
                        @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer page,
                        @Parameter(description = "Page size") @RequestParam(defaultValue = "10") Integer size) {
                PagedResponse<AcademicResourceInternalResponse> response = academicResourceService.getResourcesByCreatorId( page, size);
                return responseHandler.response(200, "Internal academic resource retrieved successfully", response);
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update academic resource", description = "Update an existing academic resource")
        public ResponseEntity<Object> updateResource(
                        @Parameter(description = "Resource ID") @PathVariable Long id,
                        @Valid @RequestBody AcademicResourceUpdateRequest request) {

                AcademicResourceResponse response = academicResourceService.updateResource(id, request);
                return responseHandler.response(200, "Academic resource updated successfully", response);
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Delete academic resource", description = "Delete an academic resource and its associated file")
        public ResponseEntity<Object> deleteResource(
                        @Parameter(description = "Resource ID") @PathVariable Long id) {

                academicResourceService.deleteResource(id);
                return responseHandler.response(200, "Academic resource deleted successfully", null);
        }

        @GetMapping("/search")
        @Operation(summary = "Search academic resources", description = "Search and filter academic resources with pagination")
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
                return responseHandler.response(200, "Academic resources retrieved successfully", response);
        }


}
