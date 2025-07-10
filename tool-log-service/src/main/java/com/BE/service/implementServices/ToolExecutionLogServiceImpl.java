package com.BE.service.implementServices;

import com.BE.mapper.ToolExecutionLogMapper;
import com.BE.model.entity.ToolExecutionLog;
import com.BE.model.request.ToolExecutionLogRequest;
import com.BE.model.request.ToolExecutionLogSearchRequest;
import com.BE.model.response.ToolExecutionLogResponse;
import com.BE.repository.ToolExecutionLogRepository;
import com.BE.service.interfaceServices.IToolExecutionLogService;
import com.BE.utils.DateNowUtils;
import com.BE.utils.PageUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ToolExecutionLogServiceImpl implements IToolExecutionLogService {

    ToolExecutionLogRepository repository;
    ToolExecutionLogMapper mapper;
    DateNowUtils dateNowUtils;
    PageUtil pageUtil;

    @Override
    public ToolExecutionLogResponse save(ToolExecutionLogRequest request) {
        ToolExecutionLog log = mapper.toEntity(request);
        log.setCreatedAt(dateNowUtils.getCurrentDateTimeHCM());
        ToolExecutionLog saved = repository.save(log);
        return mapper.toResponse(saved);
    }

    @Override
    public Page<ToolExecutionLogResponse> getAll(ToolExecutionLogSearchRequest request) {
        pageUtil.checkOffset(request.getOffset());
        Pageable pageable = pageUtil.getPageable(request.getOffset() - 1, request.getPageSize(), request.getSortBy(), request.getSortDirection());

        Specification<ToolExecutionLog> spec = Specification.where(null);

        if (request.getSearch() != null && !request.getSearch().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("input")), "%" + request.getSearch().toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("output")), "%" + request.getSearch().toLowerCase() + "%")
            ));
        }

        if (request.getToolType() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("toolType"), request.getToolType()));
        }

        if (request.getUserId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("userId"), request.getUserId()));
        }

        return repository.findAll(spec, pageable).map(mapper::toResponse);
    }
}
