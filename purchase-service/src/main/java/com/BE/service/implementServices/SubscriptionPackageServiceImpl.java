package com.BE.service.implementServices;

import com.BE.enums.SubscriptionStatus;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.SubscriptionPackageMapper;
import com.BE.model.entity.SubscriptionPackage;
import com.BE.model.request.SubscriptionPackageRequest;
import com.BE.model.response.SubscriptionPackageResponse;
import com.BE.repository.SubscriptionPackageRepository;
import com.BE.service.interfaceServices.ISubscriptionPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionPackageServiceImpl implements ISubscriptionPackageService {

    private final SubscriptionPackageRepository repository;
    private final SubscriptionPackageMapper mapper;

    @Override
    public SubscriptionPackageResponse create(SubscriptionPackageRequest request) {
        if (repository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Tên gói dịch vụ này đã tồn tại");
        }
        SubscriptionPackage entity = mapper.toEntity(request);
        entity.setStatus(SubscriptionStatus.ACTIVE);
        return mapper.toResponse(repository.save(entity));
    }


    @Override
    public List<SubscriptionPackageResponse> getAll(SubscriptionStatus status, String sortBy, String sortDirection) {
        Specification<SubscriptionPackage> spec = Specification.where(null);

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        return repository.findAll(spec, sort).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionPackageResponse changeStatus(UUID id) {
        SubscriptionPackage pkg = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Gói dịch vụ này không tồn tại"));
        pkg.setStatus(SubscriptionStatus.ACTIVE.equals(pkg.getStatus())  ? SubscriptionStatus.INACTIVE : SubscriptionStatus.ACTIVE);
        return mapper.toResponse(repository.save(pkg));
    }

    @Override
    public SubscriptionPackageResponse getById(UUID id) {
        return mapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Gói dịch vụ này không tồn tại")));
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }


    @Override
    public SubscriptionPackageResponse update(UUID id, SubscriptionPackageRequest request) {
        SubscriptionPackage entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Gói dịch vụ này không tồn tại"));
        mapper.updateFromDto(request, entity);
        return mapper.toResponse(repository.save(entity));
    }
}