package com.BE.service.interfaceServices;

import com.BE.enums.StatusEnum;
import com.BE.model.request.GradingSessionFilterRequest;
import com.BE.model.request.GradingSessionRequest;
import com.BE.model.request.GradingSessionUpdateRequest;
import com.BE.model.response.GradingSessionResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface GradingSessionService {
    GradingSessionResponse create(GradingSessionRequest request);
    Page<GradingSessionResponse> getAllWithFilter(GradingSessionFilterRequest request);

    GradingSessionResponse updateStatus(Long id, StatusEnum status);

    GradingSessionResponse getById(Long id);

    GradingSessionResponse update(Long id, GradingSessionUpdateRequest request);
}

