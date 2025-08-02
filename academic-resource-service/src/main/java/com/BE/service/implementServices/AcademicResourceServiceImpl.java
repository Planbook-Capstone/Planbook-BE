package com.BE.service.implementServices;

import com.BE.enums.AcademicResourceEnum;

import com.BE.exception.ResourceNotFoundException;
import com.BE.exception.exceptions.BadRequestException;
import com.BE.mapper.AcademicResourceMapper;
import com.BE.model.entity.AcademicResource;
import com.BE.model.entity.ResourceTag;
import com.BE.model.entity.Tag;
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
import com.BE.utils.PageUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
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
    private final PageUtil pageUtil;

    @Transactional
    public AcademicResourceResponse createResource(AcademicResourceCreateRequest request) {

        // Create academic resource
        AcademicResource resource = new AcademicResource();
        resource.setLessonId(request.getLessonId());
        resource.setType(request.getType());
        resource.setName(request.getName());
        resource.setDescription(request.getDescription());
        resource.setUrl(request.getUrl());
        resource.setCreatedBy(accountUtils.getCurrentUserId());
        //
        resource.setVisibility(AcademicResourceEnum.EXTERNAL);

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
        UUID userId = accountUtils.getCurrentUserId();
        AcademicResource resource = new AcademicResource();
        resource.setName("user_" + userId + "_resource_" + dateNowUtils.getCurrentDateTimeHCM());
        resource.setDescription("Uploaded by user " + userId);
        resource.setVisibility(AcademicResourceEnum.INTERNAL);
        resource.setCreatedBy(userId);
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

    public PagedResponse<AcademicResourceInternalResponse> getResourcesByCreatorId(int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        UUID creatorId = accountUtils.getCurrentUserId();
        Page<AcademicResource> resourcePage = academicResourceRepository.findAcademicResourceByCreatedBy(creatorId, pageable);
        return convertToInternalPagedResponse(resourcePage);
    }

    public PagedResponse<AcademicResourceResponse> searchResources(AcademicResourceSearchRequest request) {
        pageUtil.checkOffset(request.getPage());
        // Create pageable
        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection().name()), request.getSortBy().name());
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);

        Specification<AcademicResource> spec = buildSpecification(request);
        Page<AcademicResource> resourcePage = academicResourceRepository.findAll(spec, pageable);

        List<AcademicResourceResponse> responses = resourcePage.getContent().stream()
                .map(this::convertToResponse)
                .toList();

        PagedResponse<AcademicResourceResponse> response = new PagedResponse<>();
        response.setContent(responses);
        response.setPage(resourcePage.getNumber() + 1);
        response.setSize(resourcePage.getSize());
        response.setTotalElements(resourcePage.getTotalElements());
        response.setTotalPages(resourcePage.getTotalPages());
        response.setFirst(resourcePage.isFirst());
        response.setLast(resourcePage.isLast());
        response.setHasNext(resourcePage.hasNext());
        response.setHasPrevious(resourcePage.hasPrevious());

        return response;

    }

    public static Specification<AcademicResource> buildSpecification(AcademicResourceSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
                String kw = "%" + request.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), kw),
                        cb.like(cb.lower(root.get("description")), kw)
                ));
            }

            if (request.getType() != null && !request.getType().isBlank()) {
                predicates.add(cb.equal(root.get("type"), request.getType()));
            }

            if (request.getLessonId() != null) {
                predicates.add(cb.equal(root.get("lessonId"), request.getLessonId()));
            }

            if (request.getCreatedBy() != null) {
                predicates.add(cb.equal(root.get("createdBy"), request.getCreatedBy()));
            }

            if (request.getVisibility() != null) {
                predicates.add(cb.equal(root.get("visibility"), request.getVisibility()));
            }

            if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
                Join<AcademicResource, ResourceTag> tagJoin = root.join("resourceTags", JoinType.INNER);
                predicates.add(tagJoin.get("tag").get("id").in(request.getTagIds()));
                query.distinct(true); // tránh trùng do join
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


    private void assignTagsToResource(Long resourceId, Set<Long> tagIds) {
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
        response.setLessonId(resource.getLessonId());
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
