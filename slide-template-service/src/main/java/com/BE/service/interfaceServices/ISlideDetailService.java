package com.BE.service.interfaceServices;

import com.BE.model.response.SlideDetailResponse;

import java.util.List;
import java.util.Map;

public interface ISlideDetailService {

    SlideDetailResponse getSlideDetail(String id);

    Map<String, Object> getSlideDetailsByTemplateId(Long templateId);

    void processSlideDetailsFromTemplate(Long templateId, String slideDataJson);
}
