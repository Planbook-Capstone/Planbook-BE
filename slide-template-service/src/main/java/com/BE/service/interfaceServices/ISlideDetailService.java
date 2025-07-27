package com.BE.service.interfaceServices;

import com.BE.model.response.SlideDetailResponse;

import java.util.List;

public interface ISlideDetailService {

    SlideDetailResponse getSlideDetail(String id);

    List<SlideDetailResponse> getSlideDetailsByTemplateId(Long templateId);

    void processSlideDetailsFromTemplate(Long templateId, String slideDataJson);
}
