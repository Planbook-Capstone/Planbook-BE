package com.BE.service.interfaceServices;

import com.BE.enums.StatusEnum;
import com.BE.model.request.OmrTemplateRequest;
import com.BE.model.response.OmrTemplateResponse;

import java.util.List;

public interface OmrTemplateService {
    OmrTemplateResponse create(OmrTemplateRequest request);
    List<OmrTemplateResponse> getAll();
    OmrTemplateResponse getById(Long id);
    OmrTemplateResponse update(Long id, OmrTemplateRequest request);
    OmrTemplateResponse updateStatus(Long id, StatusEnum status);
}

