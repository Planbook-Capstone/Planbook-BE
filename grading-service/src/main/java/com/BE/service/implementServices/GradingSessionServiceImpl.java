package com.BE.service.implementServices;

import com.BE.enums.StatusEnum;
import com.BE.exception.ResourceNotFoundException;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.GradingSessionMapper;
import com.BE.model.entity.GradingSession;
import com.BE.model.entity.OmrTemplate;
import com.BE.model.request.GradingSessionFilterRequest;
import com.BE.model.request.GradingSessionRequest;
import com.BE.model.request.GradingSessionUpdateRequest;
import com.BE.model.response.GradingSessionResponse;
import com.BE.repository.GradingSessionRepository;
import com.BE.repository.OmrTemplateRepository;
import com.BE.service.interfaceServices.GradingSessionService;
import com.BE.utils.AccountUtils;
import com.BE.utils.PageUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradingSessionServiceImpl implements GradingSessionService {

    private final GradingSessionRepository gradingSessionRepository;
    private final OmrTemplateRepository omrTemplateRepository;
    private final GradingSessionMapper gradingSessionMapper;
    private final AccountUtils accountUtils;
    private final PageUtil pageUtil;


    @Override
    public GradingSessionResponse create(GradingSessionRequest request) {
        OmrTemplate omrTemplate = omrTemplateRepository.findById(request.getOmrTemplateId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy mẫu OMR với ID: " + request.getOmrTemplateId()));

        GradingSession gradingSession = gradingSessionMapper.toEntity(request);
        gradingSession.setOmrTemplate(omrTemplate);
        gradingSession.setUserId(accountUtils.getCurrentUserId());
        gradingSession.setStatus(StatusEnum.ACTIVE);

        GradingSession savedSession = gradingSessionRepository.save(gradingSession);
        return gradingSessionMapper.toResponse(savedSession);
    }

    @Override
    public Page<GradingSessionResponse> getAllWithFilter(GradingSessionFilterRequest request) {
        pageUtil.checkOffset(request.getPage());

        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                request.getSize(),
                Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy())
        );

        Specification<GradingSession> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (request.getName() != null) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + request.getName().toLowerCase() + "%"));
            }
            if (request.getBookTypeId() != null) {
                predicates.add(cb.equal(root.get("bookTypeId"), request.getBookTypeId()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }
            UUID userId = accountUtils.getCurrentUserId();
            if (userId != null) {
                predicates.add(cb.equal(root.get("userId"), userId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return gradingSessionRepository.findAll(spec, pageable)
                .map(gradingSessionMapper::toResponse);
    }

    @Override
    public GradingSessionResponse updateStatus(Long id, StatusEnum status) {
        GradingSession gradingSession = gradingSessionRepository.findById(id) .orElseThrow(() -> new NotFoundException("Không tìm thấy mẫu OMR với ID: " + id));
        gradingSession.setStatus(status);
        return gradingSessionMapper.toResponse(gradingSessionRepository.save(gradingSession));
    }

    @Override
    public GradingSessionResponse getById(Long id) {
        GradingSession gradingSession = gradingSessionRepository.findById(id) .orElseThrow(() -> new NotFoundException("Không tìm thấy mẫu OMR với ID: " + id));
        return gradingSessionMapper.toResponse(gradingSession);
    }

    @Override
    public GradingSessionResponse update(Long id, GradingSessionUpdateRequest request) {
        GradingSession gradingSession = gradingSessionRepository.findById(id) .orElseThrow(() -> new NotFoundException("Không tìm thấy mẫu OMR với ID: " + id));
        gradingSessionMapper.updateEntity(gradingSession, request);
        return gradingSessionMapper.toResponse(gradingSessionRepository.save(gradingSession));
    }

}

