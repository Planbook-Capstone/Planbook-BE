package com.BE.service.interfaceServices;

import com.BE.model.request.GradingSessionRequest;
import com.BE.model.response.GradingSessionResponse;

import java.util.List;
import java.util.UUID;

public interface GradingSessionService {
    GradingSessionResponse create(GradingSessionRequest request);
    List<GradingSessionResponse> getAll(UUID bookTypeId);
}

