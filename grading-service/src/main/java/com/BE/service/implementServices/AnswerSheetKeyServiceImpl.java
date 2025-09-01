package com.BE.service.implementServices;

import com.BE.exception.ResourceNotFoundException;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.AnswerSheetKeyMapper;
import com.BE.model.entity.AnswerSheetKey;
import com.BE.model.entity.GradingSession;
import com.BE.model.request.AnswerSheetKeyRequest;
import com.BE.model.response.AnswerSheetKeyResponse;
import com.BE.repository.AnswerSheetKeyRepository;
import com.BE.repository.GradingSessionRepository;
import com.BE.service.interfaceServices.AnswerSheetKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnswerSheetKeyServiceImpl implements AnswerSheetKeyService {

    private final AnswerSheetKeyRepository answerSheetKeyRepository;
    private final GradingSessionRepository gradingSessionRepository;
    private final AnswerSheetKeyMapper answerSheetKeyMapper;

    @Override
    public AnswerSheetKeyResponse create(AnswerSheetKeyRequest request) {
        GradingSession gradingSession = gradingSessionRepository.findById(request.getGradingSessionId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phiên chấm điểm với ID: " + request.getGradingSessionId()));

        AnswerSheetKey answerSheetKey = answerSheetKeyMapper.toEntity(request);
        answerSheetKey.setGradingSession(gradingSession);

        AnswerSheetKey savedKey = answerSheetKeyRepository.save(answerSheetKey);
        return answerSheetKeyMapper.toResponse(savedKey);
    }

    @Override
    public List<AnswerSheetKeyResponse> getByGradingSessionId(Long gradingSessionId) {
        if (!gradingSessionRepository.existsById(gradingSessionId)) {
            throw new NotFoundException("Không tìm thấy phiên chấm điểm với ID: " + gradingSessionId);
        }
        List<AnswerSheetKey> keys = answerSheetKeyRepository.findByGradingSessionId(gradingSessionId);
        return keys.stream()
                .map(answerSheetKeyMapper::toResponse)
                .collect(Collectors.toList());
    }
}

