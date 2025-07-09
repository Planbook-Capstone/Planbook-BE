package com.BE.service.implementServices;


import com.BE.enums.StatusEnum;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.ExternalToolConfigMapper;

import com.BE.model.entity.ExternalToolConfig;
import com.BE.model.request.ExternalToolConfigRequest;
import com.BE.model.request.ExternalToolSearchRequest;
import com.BE.model.response.ExternalToolConfigResponse;
import com.BE.repository.ExternalToolConfigRepository;
import com.BE.service.interfaceServices.IExternalToolConfigService;
import com.BE.utils.AccountUtils;
import com.BE.utils.PageUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    PageUtil pageUtil;
    AccountUtils accountUtils;




    @Override
    public ExternalToolConfigResponse create(ExternalToolConfigRequest request) {
        ExternalToolConfig config = mapper.toEntity(request);
        config.setStatus(StatusEnum.ACTIVE);
        config.setCreatedBy(accountUtils.getCurrentUserId());
        return mapper.toResponse(repository.save(config));
    }
    public Page<ExternalToolConfigResponse> getAll(ExternalToolSearchRequest request) {
        pageUtil.checkOffset(request.getOffset());

        Pageable pageable = pageUtil.getPageable(
                request.getOffset() - 1,
                request.getPageSize(),
                request.getSortBy().name(),
                request.getSortDirection().name()
        );

        Specification<ExternalToolConfig> spec = Specification.where(null);

        if (request.getSearch() != null && !request.getSearch().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("name")), "%" + request.getSearch().toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("description")), "%" + request.getSearch().toLowerCase() + "%")
                    )
            );
        }

        if (request.getStatus() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), request.getStatus())
            );
        }

        if (request.getCreatedBy() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("createdBy"), request.getCreatedBy())
            );
        }

        return repository.findAll(spec, pageable)
                .map(mapper::toResponse);
    }


    @Override
    public ExternalToolConfigResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy cấu hình với ID: " + id));
    }
}