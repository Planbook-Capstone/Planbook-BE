package com.BE.controller;

import com.BE.model.request.TagCreateRequest;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.PagedResponse;
import com.BE.model.response.TagResponse;
import com.BE.service.TagService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Tag(name = "Tag Management", description = "APIs for managing tags")
public class TagController {

        private final TagService tagService;
        private final ResponseHandler responseHandler;

        @PostMapping
        @Operation(summary = "Create a new tag", description = "Create a new tag for categorizing resources")
        public ResponseEntity<DataResponseDTO<TagResponse>> createTag(
                        @Valid @RequestBody TagCreateRequest request) {

                TagResponse response = tagService.createTag(request);
                return responseHandler.response(201, "Tag created successfully", response);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get tag by ID", description = "Retrieve a specific tag by its ID")
        public ResponseEntity<DataResponseDTO<TagResponse>> getTagById(
                        @Parameter(description = "Tag ID") @PathVariable Long id) {

                TagResponse response = tagService.getTagById(id);
                return responseHandler.response(200, "Tag retrieved successfully", response);
        }

        @GetMapping
        @Operation(summary = "Get all tags", description = "Retrieve all tags sorted by name")
        public ResponseEntity<DataResponseDTO<List<TagResponse>>> getAllTags() {

                List<TagResponse> response = tagService.getAllTags();
                return responseHandler.response(200, "Tags retrieved successfully", response);
        }

        @GetMapping("/search")
        @Operation(summary = "Search tags", description = "Search tags by keyword with pagination")
        public ResponseEntity<DataResponseDTO<PagedResponse<TagResponse>>> searchTags(
                        @Parameter(description = "Search keyword") @RequestParam(required = false) String keyword,
                        @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer page,
                        @Parameter(description = "Page size") @RequestParam(defaultValue = "10") Integer size) {

                PagedResponse<TagResponse> response = tagService.searchTags(keyword, page, size);
                return responseHandler.response(200, "Tags retrieved successfully", response);
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update tag", description = "Update an existing tag")
        public ResponseEntity<DataResponseDTO<TagResponse>> updateTag(
                        @Parameter(description = "Tag ID") @PathVariable Long id,
                        @Valid @RequestBody TagCreateRequest request) {

                TagResponse response = tagService.updateTag(id, request);
                return responseHandler.response(200, "Tag updated successfully", response);
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Delete tag", description = "Delete a tag (only if not used by any resources)")
        public ResponseEntity<DataResponseDTO<Void>> deleteTag(
                        @Parameter(description = "Tag ID") @PathVariable Long id) {

                tagService.deleteTag(id);
                return responseHandler.response(200, "Tag deleted successfully", null);
        }

        @GetMapping("/popular")
        @Operation(summary = "Get popular tags", description = "Get tags ordered by usage count")
        public ResponseEntity<DataResponseDTO<PagedResponse<Object[]>>> getPopularTags(
                        @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer page,
                        @Parameter(description = "Page size") @RequestParam(defaultValue = "10") Integer size) {

                PagedResponse<Object[]> response = tagService.getPopularTags(page, size);
                return responseHandler.response(200, "Popular tags retrieved successfully", response);
        }

        @GetMapping("/resource/{resourceId}")
        @Operation(summary = "Get tags by resource", description = "Get all tags associated with a specific resource")
        public ResponseEntity<DataResponseDTO<List<TagResponse>>> getTagsByResourceId(
                        @Parameter(description = "Resource ID") @PathVariable Long resourceId) {

                List<TagResponse> response = tagService.getTagsByResourceId(resourceId);
                return responseHandler.response(200, "Resource tags retrieved successfully", response);
        }

        @GetMapping("/unused")
        @Operation(summary = "Get unused tags", description = "Get tags that are not associated with any resources")
        public ResponseEntity<DataResponseDTO<List<TagResponse>>> getUnusedTags() {

                List<TagResponse> response = tagService.getUnusedTags();
                return responseHandler.response(200, "Unused tags retrieved successfully", response);
        }
}
