package com.BE.service.interfaceServices;

import com.BE.enums.StatusEnum;
import com.BE.model.request.OmrTemplateFilterRequest;
import com.BE.model.request.OmrTemplateRequest;
import com.BE.model.response.OmrTemplateResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OmrTemplateService {
    OmrTemplateResponse create(OmrTemplateRequest request);
    Page<OmrTemplateResponse> getAllFiltered(OmrTemplateFilterRequest request);
    OmrTemplateResponse getById(Long id);
    OmrTemplateResponse update(Long id, OmrTemplateRequest request);
    OmrTemplateResponse updateStatus(Long id, StatusEnum status);
}

