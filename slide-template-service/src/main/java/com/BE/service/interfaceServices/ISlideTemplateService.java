package com.BE.service.interfaceServices;

import com.BE.model.entity.SlideTemplate;
import com.BE.model.request.SlideTemplateRequest;
import com.BE.model.response.SlideTemplateResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ISlideTemplateService {

    SlideTemplate saveSlideTemplate(SlideTemplateRequest slideTemplateRequest);

    SlideTemplateResponse getSlideTemplate(Long id);

    Page<SlideTemplateResponse> getAllSlideTemplates(
            int page, int size, String search, String status, String sortBy, String sortDirection
    );
    SlideTemplateResponse updateSlideTemplate(Long id, SlideTemplateRequest slideTemplateRequest);

    SlideTemplateResponse changeSlideTemplateStatus(long id, String newStatus);

}
