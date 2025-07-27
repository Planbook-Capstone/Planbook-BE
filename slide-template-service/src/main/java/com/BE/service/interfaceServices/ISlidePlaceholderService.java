package com.BE.service.interfaceServices;

import com.BE.model.entity.SlidePlaceholder;
import com.BE.model.request.SlidePlaceholderRequest;
import com.BE.model.response.SlidePlaceholderResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ISlidePlaceholderService {

    SlidePlaceholder saveSlidePlaceholder(SlidePlaceholderRequest request);

    SlidePlaceholderResponse getSlidePlaceholder(Long id);

    List<SlidePlaceholderResponse> getAllSlidePlaceholders();

    Page<SlidePlaceholderResponse> getAllSlidePlaceholders(
            int page, int size, String search, String status, String sortBy, String sortDirection
    );

    SlidePlaceholderResponse updateSlidePlaceholder(Long id, SlidePlaceholderRequest request);

    SlidePlaceholderResponse changeSlidePlaceholderStatus(long id, String newStatus);
}
