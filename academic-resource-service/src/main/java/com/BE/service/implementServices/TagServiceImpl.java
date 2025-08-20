package com.BE.service.implementServices;

import com.BE.exception.AcademicResourceException;
import com.BE.exception.ResourceNotFoundException;
import com.BE.model.entity.Tag;
import com.BE.model.request.TagCreateRequest;
import com.BE.model.response.PagedResponse;
import com.BE.model.response.TagResponse;
import com.BE.repository.ResourceTagRepository;
import com.BE.repository.TagRepository;
import com.BE.service.interfaceServices.TagService;
import com.BE.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final ResourceTagRepository resourceTagRepository;
    private final AccountUtils accountUtils;

    @Transactional
    public TagResponse createTag(TagCreateRequest request) {
        // Check if tag already exists
        if (tagRepository.existsByNameIgnoreCase(request.getName())) {
            throw new AcademicResourceException("Loại học liệu với tên '" + request.getName() + "' đã tồn tại");
        }

        Tag tag = new Tag();
        tag.setName(request.getName());
        tag.setDescription(request.getDescription());
        tag.setCreatedBy(accountUtils.getCurrentUserId());
        tag = tagRepository.save(tag);

        return convertToResponse(tag);
    }

    public TagResponse getTagById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loại học liệu", id));

        return convertToResponse(tag);
    }

    public List<TagResponse> getAllTags() {
        List<Tag> tags = tagRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));

        return tags.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public PagedResponse<TagResponse> searchTags(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));

        Page<Tag> tagPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            tagPage = tagRepository.findByKeyword(keyword, pageable);
        } else {
            tagPage = tagRepository.findAll(pageable);
        }

        return convertToPagedResponse(tagPage);
    }

    @Transactional
    public TagResponse updateTag(Long id, TagCreateRequest request) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loại học liệu", id));

        // Check if new name conflicts with existing tag (excluding current tag)
        if (!tag.getName().equalsIgnoreCase(request.getName()) &&
                tagRepository.existsByNameIgnoreCase(request.getName())) {
            throw new AcademicResourceException("Loại học liệu với tên '" + request.getName() + "' đã tồn tại");
        }
        if (request.getName() != null || !request.getName().trim().isEmpty()) {
            tag.setName(request.getName());
        }
        if (request.getDescription() != null || !request.getDescription().trim().isEmpty()) {
             tag.setDescription(request.getDescription());
        }
        tag = tagRepository.save(tag);

        return convertToResponse(tag);
    }

    @Transactional
    public void deleteTag(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loại học liệu", id));

        // Check if tag is being used by any resources
        long resourceCount = resourceTagRepository.countByTagId(id);
        if (resourceCount > 0) {
            throw new AcademicResourceException(
                    "Không thể xóa loại học liệu vì đang được sử dụng bởi " + resourceCount + " tài nguyên.");
        }

        tagRepository.delete(tag);
    }

    public PagedResponse<Object[]> getPopularTags(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> popularTags = tagRepository.findPopularTags(pageable);

        PagedResponse<Object[]> response = new PagedResponse<>();
        response.setContent(popularTags.getContent());
        response.setPage(popularTags.getNumber());
        response.setSize(popularTags.getSize());
        response.setTotalElements(popularTags.getTotalElements());
        response.setTotalPages(popularTags.getTotalPages());
        response.setFirst(popularTags.isFirst());
        response.setLast(popularTags.isLast());
        response.setHasNext(popularTags.hasNext());
        response.setHasPrevious(popularTags.hasPrevious());

        return response;
    }

    public List<TagResponse> getTagsByResourceId(Long resourceId) {
        List<Tag> tags = tagRepository.findByResourceId(resourceId);

        return tags.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<TagResponse> getUnusedTags() {
        List<Tag> unusedTags = tagRepository.findUnusedTags();

        return unusedTags.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private TagResponse convertToResponse(Tag tag) {
        TagResponse response = new TagResponse();
        response.setId(tag.getId());
        response.setName(tag.getName());
        response.setDescription(tag.getDescription());

        return response;
    }

    private PagedResponse<TagResponse> convertToPagedResponse(Page<Tag> page) {
        List<TagResponse> content = page.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        PagedResponse<TagResponse> response = new PagedResponse<>();
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
