package com.BE.service.implementServices;


import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.ExternalToolConfigMapper;

import com.BE.model.entity.ExternalToolConfig;
import com.BE.model.request.ExternalToolConfigRequest;
import com.BE.model.response.ExternalToolConfigResponse;
import com.BE.repository.ExternalToolConfigRepository;
import com.BE.service.interfaceServices.IExternalToolConfigService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ExternalToolConfigServiceImpl implements IExternalToolConfigService {

    ExternalToolConfigRepository repository;
    ExternalToolConfigMapper mapper;


    @Override
    public ExternalToolConfigResponse create(ExternalToolConfigRequest request, UUID createdBy) {
        ExternalToolConfig config = mapper.toEntity(request);
        config.setCreatedBy(createdBy);
        return mapper.toResponse(repository.save(config));
    }

//    public ExternalToolConfigResponse getById(Long id) {
//        ExternalToolConfig config = repository.findById(id)
//                .orElseThrow(() -> new NotFoundException("Không tìm thấy cấu hình với ID: " + id));
//        return mapper.toResponse(config);
//    }
//
//    public List<ExternalToolConfigResponse> getAll() {
//        return repository.findAll()
//                .stream()
//                .map(mapper::toResponse)
//                .collect(Collectors.toList());
//    }
//
//    public ExternalToolConfigResponse update(Long id, ExternalToolConfigUpdateRequest request) {
//        ExternalToolConfig existing = repository.findById(id)
//                .orElseThrow(() -> new NotFoundException("Không tìm thấy cấu hình với ID: " + id));
//
//        existing.setName(request.getName());
//        existing.setApiUrl(request.getApiUrl());
//        existing.setTokenUrl(request.getTokenUrl());
//        existing.setClientId(request.getClientId());
//        existing.setClientSecret(request.getClientSecret());
//        existing.setDescription(request.getDescription());
//        existing.setStatus(request.getStatus());
//
//        return mapper.toResponse(repository.save(existing));
//    }
//
//    public void delete(Long id) {
//        if (!repository.existsById(id)) {
//            throw new NotFoundException("Không tìm thấy cấu hình với ID: " + id);
//        }
//        repository.deleteById(id);
//    }
}

