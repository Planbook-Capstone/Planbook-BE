package com.BE.service.interfaceServices;

import com.BE.model.request.AnswerSheetKeyRequest;
import com.BE.model.request.AnswerSheetKeyUpdateRequest;
import com.BE.model.request.GradingSessionUpdateRequest;
import com.BE.model.response.AnswerSheetKeyResponse;

import java.util.List;

public interface AnswerSheetKeyService {
    AnswerSheetKeyResponse create(AnswerSheetKeyRequest request);
    List<AnswerSheetKeyResponse> getByGradingSessionId(Long gradingSessionId);

    AnswerSheetKeyResponse update(Long id, AnswerSheetKeyUpdateRequest request);

    String delete(Long id);

}

