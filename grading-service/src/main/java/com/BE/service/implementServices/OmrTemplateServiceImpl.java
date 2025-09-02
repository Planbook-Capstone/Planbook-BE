package com.BE.service.implementServices;

import com.BE.enums.StatusEnum;
import com.BE.exception.ResourceNotFoundException;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.OmrTemplateMapper;
import com.BE.model.entity.OmrTemplate;
import com.BE.model.request.OmrTemplateFilterRequest;
import com.BE.model.request.OmrTemplateRequest;
import com.BE.model.response.OmrTemplateResponse;
import com.BE.repository.OmrTemplateRepository;
import com.BE.service.interfaceServices.OmrTemplateService;
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


@Service
@RequiredArgsConstructor
public class OmrTemplateServiceImpl implements OmrTemplateService {

    private final OmrTemplateRepository omrTemplateRepository;
    private final OmrTemplateMapper omrTemplateMapper;
    private final PageUtil pageUtil;

    @Override
    public OmrTemplateResponse create(OmrTemplateRequest request) {
        OmrTemplate omrTemplate = omrTemplateMapper.toEntity(request);
        omrTemplate.setStatus(StatusEnum.ACTIVE);
        OmrTemplate savedOmrTemplate = omrTemplateRepository.save(omrTemplate);
        return omrTemplateMapper.toResponse(savedOmrTemplate);
    }

    @Override
    public Page<OmrTemplateResponse> getAllFiltered(OmrTemplateFilterRequest request) {
        pageUtil.checkOffset(request.getPage());
        Specification<OmrTemplate> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getName() != null && !request.getName().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + request.getName().toLowerCase() + "%"));
            }

            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                request.getSize(),
                Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy())
        );

        return omrTemplateRepository.findAll(spec, pageable)
                .map(omrTemplateMapper::toResponse);
    }


    @Override
    public OmrTemplateResponse getById(Long id) {
        OmrTemplate omrTemplate = omrTemplateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy mẫu OMR với ID: " + id));
        return omrTemplateMapper.toResponse(omrTemplate);
    }

    @Override
    public OmrTemplateResponse update(Long id, OmrTemplateRequest request) {
        OmrTemplate existingOmrTemplate = omrTemplateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy mẫu OMR với ID: " + id));
        omrTemplateMapper.updateEntityFromRequest(request, existingOmrTemplate);
        OmrTemplate updatedOmrTemplate = omrTemplateRepository.save(existingOmrTemplate);
        return omrTemplateMapper.toResponse(updatedOmrTemplate);
    }

    @Override
    public OmrTemplateResponse updateStatus(Long id, StatusEnum status) {

        OmrTemplate omrTemplate = omrTemplateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy mẫu OMR với ID: " + id));
        omrTemplate.setStatus(status);
        return omrTemplateMapper.toResponse(omrTemplateRepository.save(omrTemplate));
    }

}

