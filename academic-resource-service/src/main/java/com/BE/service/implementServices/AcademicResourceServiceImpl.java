package com.BE.service.implementServices;

import com.BE.enums.AcademicResourceEnum;
import com.BE.enums.RoleEnum;
import com.BE.exception.ResourceNotFoundException;
import com.BE.exception.exceptions.BadRequestException;
import com.BE.mapper.AcademicResourceMapper;
import com.BE.model.AcademicResource;
import com.BE.model.ResourceTag;
import com.BE.model.Tag;
import com.BE.model.request.AcademicResourceCreateRequest;
import com.BE.model.request.AcademicResourceCreateWithFileRequest;
import com.BE.model.request.AcademicResourceSearchRequest;
import com.BE.model.request.AcademicResourceUpdateRequest;
import com.BE.model.response.*;
import com.BE.repository.AcademicResourceRepository;
import com.BE.repository.ResourceTagRepository;
import com.BE.repository.TagRepository;
import com.BE.service.interfaceServices.AcademicResourceService;
import com.BE.service.interfaceServices.SupabaseStorageService;
import com.BE.utils.AccountUtils;
import com.BE.utils.DateNowUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AcademicResourceServiceImpl implements AcademicResourceService {

    private final AcademicResourceRepository academicResourceRepository;
    private final TagRepository tagRepository;
    private final ResourceTagRepository resourceTagRepository;
    private final SupabaseStorageService supabaseStorageService;
    private final DateNowUtils dateNowUtils;
    private final AccountUtils accountUtils;
    private final AcademicResourceMapper academicResourceMapper;

    @Transactional
    public AcademicResourceResponse createResource(AcademicResourceCreateRequest request) {

        // Create academic resource
        AcademicResource resource = new AcademicResource();
        resource.setType(request.getType());
        resource.setName(request.getName());
        resource.setDescription(request.getDescription());
        resource.setUrl(request.getUrl());
        resource.setCreatedAt(dateNowUtils.dateNow());
        resource.setCreatedBy(accountUtils.getCurrentUserId());
        //
        List<String> roles = accountUtils.getCurrentUserRoles();
        System.out.println("Current user roles: " + roles);
        if (roles.contains(RoleEnum.ROLE_ADMIN.name()) || roles.contains(RoleEnum.ROLE_STAFF.name())) {
            resource.setVisibility(AcademicResourceEnum.EXTERNAL);
        }
        // Save resource
        resource = academicResourceRepository.save(resource);

        // Handle tags
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            assignTagsToResource(resource.getId(), request.getTagIds());
        }

        return convertToResponse(resource);
    }

    @Transactional
    public AcademicResourceInternalResponse createResourceInternal(MultipartFile file){
        FileUploadResponse uploadResponse = null;
        uploadResponse = supabaseStorageService.uploadFile(file);
        String userId = accountUtils.getCurrentUserId();
        List<String> roles = accountUtils.getCurrentUserRoles();
        AcademicResource resource = new AcademicResource();
        resource.setName("user_" + userId + "_resource_" + dateNowUtils.getCurrentDateTimeHCM());
        resource.setDescription("Uploaded by user " + userId);
        resource.setVisibility(AcademicResourceEnum.INTERNAL);
        resource.setCreatedBy(userId);
        resource.setCreatedAt(dateNowUtils.dateNow());
        resource.setUrl(uploadResponse.getFileUrl());
        resource = academicResourceRepository.save(resource);
        return academicResourceMapper.toInternalResponse(resource);
    }

    @Transactional
    public AcademicResourceResponse createResourceWithFile(AcademicResourceCreateWithFileRequest request) {
        // Upload file first
        FileUploadResponse uploadResponse = null;

        AcademicResourceCreateRequest createRequest = null;
        try {
            System.out.println("Metadata JSON: " + request.getMetadataJson());
            createRequest = new ObjectMapper().readValue(request.getMetadataJson(),
                    AcademicResourceCreateRequest.class);
            System.out.println("Parsed Create Request: " + createRequest);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid metadata JSON format: " + e.getMessage());
        }
        uploadResponse = supabaseStorageService.uploadFile(request.getFile());

        // Set the uploaded file URL
        createRequest.setUrl(uploadResponse.getFileUrl());
        // Create resource
        return createResource(createRequest);
    }

    public AcademicResourceResponse getResourceById(Long id) {
        AcademicResource resource = academicResourceRepository.findByIdWithTags(id)
                .orElseThrow(() -> new ResourceNotFoundException("Academic resource", id));

        return convertToResponse(resource);
    }

    @Transactional
    public AcademicResourceResponse updateResource(Long id, AcademicResourceUpdateRequest request) {
        AcademicResource resource = academicResourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Academic resource", id));

        // Update fields if provided
        if (request.getType() != null) {
            resource.setType(request.getType());
        }
        if (request.getName() != null) {
            resource.setName(request.getName());
        }
        if (request.getDescription() != null) {
            resource.setDescription(request.getDescription());
        }
        if (request.getUrl() != null) {
            resource.setUrl(request.getUrl());
        }
        resource.setUpdatedAt(dateNowUtils.dateNow());
        // Save resource
        resource = academicResourceRepository.save(resource);

        // Update tags if provided
        if (request.getTagIds() != null) {
            // Remove existing tags
            resourceTagRepository.deleteByResourceId(id);

            // Add new tags
            if (!request.getTagIds().isEmpty()) {
                assignTagsToResource(id, request.getTagIds());
            }
        }

        return convertToResponse(resource);
    }

    @Transactional
    public void deleteResource(Long id) {
        AcademicResource resource = academicResourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Academic resource", id));

        // Delete file from storage if it's a Supabase URL
        String fileName = supabaseStorageService.extractFileNameFromUrl(resource.getUrl());
        if (fileName != null) {
            supabaseStorageService.deleteFile(fileName);
        }

        // Delete resource (cascade will handle resource_tag relationships)
        academicResourceRepository.delete(resource);
    }

    public PagedResponse<AcademicResourceInternalResponse> getResourcesByCreatorId(String creatorId, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AcademicResource> resourcePage = academicResourceRepository.findAcademicResourceByCreatedBy(creatorId, pageable);
        return convertToInternalPagedResponse(resourcePage);
    }

    public PagedResponse<AcademicResourceResponse> searchResources(AcademicResourceSearchRequest request) {
        // Create pageable
        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<AcademicResource> resourcePage;

        // Perform search based on filters
        if (hasMultipleFilters(request)) {
            resourcePage = academicResourceRepository.findByFilters(
                    request.getKeyword(),
                    request.getType(),
                    request.getTagIds(),
                    pageable);
        } else if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            resourcePage = academicResourceRepository.findByKeyword(request.getKeyword(), pageable);
        } else if (request.getType() != null && !request.getType().trim().isEmpty()) {
            resourcePage = academicResourceRepository.findByTypeIgnoreCase(request.getType(), pageable);
        } else if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            resourcePage = academicResourceRepository.findByTagIds(request.getTagIds(), pageable);
        } else {
            resourcePage = academicResourceRepository.findAll(pageable);
        }

        return convertToPagedResponse(resourcePage);
    }

    private boolean hasMultipleFilters(AcademicResourceSearchRequest request) {
        int filterCount = 0;
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty())
            filterCount++;
        if (request.getType() != null && !request.getType().trim().isEmpty())
            filterCount++;
        if (request.getTagIds() != null && !request.getTagIds().isEmpty())
            filterCount++;

        return filterCount > 1;
    }

    @Transactional
    protected void assignTagsToResource(Long resourceId, Set<Long> tagIds) {
        List<Tag> tags = tagRepository.findByIdIn(tagIds);

        // Get the resource entity
        AcademicResource resource = academicResourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Academic resource", resourceId));

        for (Tag tag : tags) {
            ResourceTag resourceTag = new ResourceTag();
            resourceTag.setResource(resource); // Set entity object, not ID
            resourceTag.setTag(tag); // Set entity object, not ID
            resourceTagRepository.save(resourceTag);
        }
    }

    private AcademicResourceResponse convertToResponse(AcademicResource resource) {
        AcademicResourceResponse response = new AcademicResourceResponse();
        response.setId(resource.getId());
        response.setType(resource.getType());
        response.setName(resource.getName());
        response.setDescription(resource.getDescription());
        response.setUrl(resource.getUrl());
        response.setVisibility(String.valueOf(resource.getVisibility()));
        response.setCreatedBy(resource.getCreatedBy());
        response.setCreatedAt(resource.getCreatedAt());
        response.setUpdatedAt(resource.getUpdatedAt());

        // Convert tags
        Set<ResourceTag> rs = new HashSet<>(resourceTagRepository.findByResource_Id(resource.getId()));

        if (!rs.isEmpty()) {
            Set<TagResponse> tagResponses = rs.stream()
                    .map(rt -> {
                        TagResponse tagResponse = new TagResponse();
                        tagResponse.setId(rt.getTag().getId());
                        tagResponse.setName(rt.getTag().getName());
                        tagResponse.setDescription(rt.getTag().getDescription());
                        return tagResponse;
                    })
                    .collect(Collectors.toSet());
            response.setTags(tagResponses);
        } else {
            response.setTags(new HashSet<>());
        }

        return response;
    }

    private PagedResponse<AcademicResourceResponse> convertToPagedResponse(Page<AcademicResource> page) {
        List<AcademicResourceResponse> content = page.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        PagedResponse<AcademicResourceResponse> response = new PagedResponse<>();
        response.setContent(content);
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());
        response.setHasNext(page.hasNext());
        response.setHasPrevious(page.hasPrevious());

        return response;
    }
    private PagedResponse<AcademicResourceInternalResponse> convertToInternalPagedResponse(Page<AcademicResource> page) {
        List<AcademicResourceInternalResponse> content = page.getContent().stream()
                .map(academicResourceMapper::toInternalResponse)
                .collect(Collectors.toList());
        PagedResponse<AcademicResourceInternalResponse> response = new PagedResponse<>();
        response.setContent(content);
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());
        response.setHasNext(page.hasNext());
        response.setHasPrevious(page.hasPrevious());

        return response;
    }
}
