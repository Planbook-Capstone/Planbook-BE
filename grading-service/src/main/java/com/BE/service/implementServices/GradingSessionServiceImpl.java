package com.BE.service.implementServices;

import com.BE.exception.ResourceNotFoundException;
import com.BE.mapper.GradingSessionMapper;
import com.BE.model.entity.GradingSession;
import com.BE.model.entity.OmrTemplate;
import com.BE.model.request.GradingSessionRequest;
import com.BE.model.response.GradingSessionResponse;
import com.BE.repository.GradingSessionRepository;
import com.BE.repository.OmrTemplateRepository;
import com.BE.service.interfaceServices.GradingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradingSessionServiceImpl implements GradingSessionService {

    private final GradingSessionRepository gradingSessionRepository;
    private final OmrTemplateRepository omrTemplateRepository;
    private final GradingSessionMapper gradingSessionMapper;

    @Override
    public GradingSessionResponse create(GradingSessionRequest request) {
        OmrTemplate omrTemplate = omrTemplateRepository.findById(request.getOmrTemplateId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mẫu OMR với ID: " + request.getOmrTemplateId()));

        GradingSession gradingSession = gradingSessionMapper.toEntity(request);
        gradingSession.setOmrTemplate(omrTemplate);

        GradingSession savedSession = gradingSessionRepository.save(gradingSession);
        return gradingSessionMapper.toResponse(savedSession);
    }

    @Override
    public List<GradingSessionResponse> getAll(UUID bookTypeId) {
        List<GradingSession> sessions;
        if (bookTypeId != null) {
            sessions = gradingSessionRepository.findByBookTypeId(bookTypeId);
        } else {
            sessions = gradingSessionRepository.findAll();
        }
        return sessions.stream()
                .map(gradingSessionMapper::toResponse)
                .collect(Collectors.toList());
    }
}

