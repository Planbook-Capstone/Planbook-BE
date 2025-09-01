package com.BE.service.implementServices;

import com.BE.enums.StatusEnum;
import com.BE.exception.ResourceNotFoundException;
import com.BE.mapper.OmrTemplateMapper;
import com.BE.model.entity.OmrTemplate;
import com.BE.model.request.OmrTemplateRequest;
import com.BE.model.response.OmrTemplateResponse;
import com.BE.repository.OmrTemplateRepository;
import com.BE.service.interfaceServices.OmrTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OmrTemplateServiceImpl implements OmrTemplateService {

    private final OmrTemplateRepository omrTemplateRepository;
    private final OmrTemplateMapper omrTemplateMapper;

    @Override
    public OmrTemplateResponse create(OmrTemplateRequest request) {
        OmrTemplate omrTemplate = omrTemplateMapper.toEntity(request);
        OmrTemplate savedOmrTemplate = omrTemplateRepository.save(omrTemplate);
        return omrTemplateMapper.toResponse(savedOmrTemplate);
    }

    @Override
    public List<OmrTemplateResponse> getAll() {
        return omrTemplateRepository.findAll().stream()
                .map(omrTemplateMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OmrTemplateResponse getById(Long id) {
        OmrTemplate omrTemplate = omrTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mẫu OMR với ID: " + id));
        return omrTemplateMapper.toResponse(omrTemplate);
    }

    @Override
    public OmrTemplateResponse update(Long id, OmrTemplateRequest request) {
        OmrTemplate existingOmrTemplate = omrTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mẫu OMR với ID: " + id));
        omrTemplateMapper.updateEntityFromRequest(request, existingOmrTemplate);
        OmrTemplate updatedOmrTemplate = omrTemplateRepository.save(existingOmrTemplate);
        return omrTemplateMapper.toResponse(updatedOmrTemplate);
    }

    @Override
    public OmrTemplateResponse updateStatus(Long id, StatusEnum status) {

        OmrTemplate omrTemplate = omrTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mẫu OMR với ID: " + id));
        omrTemplate.setStatus(status);
        return omrTemplateMapper.toResponse(omrTemplateRepository.save(omrTemplate));
    }

}

