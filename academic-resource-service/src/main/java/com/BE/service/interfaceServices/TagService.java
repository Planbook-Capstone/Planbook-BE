package com.BE.service.interfaceServices;

import com.BE.model.request.TagCreateRequest;
import com.BE.model.response.PagedResponse;
import com.BE.model.response.TagResponse;

import java.util.List;

public interface TagService {
    TagResponse createTag(TagCreateRequest request);
    TagResponse getTagById(Long id);
    List<TagResponse> getAllTags();
    PagedResponse<TagResponse> searchTags(String keyword, int page, int size);
    TagResponse updateTag(Long id, TagCreateRequest request);
    void deleteTag(Long id);
    PagedResponse<Object[]> getPopularTags(int page, int size);
    List<TagResponse> getTagsByResourceId(Long resourceId);
    List<TagResponse> getUnusedTags();
}
