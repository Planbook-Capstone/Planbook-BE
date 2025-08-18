package com.BE.service.implementService;

import com.BE.enums.StatusEnum;
import com.BE.mapper.MatrixConfigMapper;
import com.BE.model.entity.MatrixConfig;
import com.BE.model.request.MatrixConfigRequest;
import com.BE.model.response.MatrixConfigResponse;
import com.BE.repository.MatrixConfigRepository;
import com.BE.service.interfaceService.IMatrixConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatrixConfigServiceImpl implements IMatrixConfigService {
    private final MatrixConfigRepository repository;
    private final MatrixConfigMapper mapper;

    @Override
    @Transactional
    public MatrixConfigResponse create(MatrixConfigRequest request) {
        MatrixConfig entity = mapper.toEntity(request);
        entity.setStatus(StatusEnum.INACTIVE);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public MatrixConfigResponse update(Long id, MatrixConfigRequest request) {
        MatrixConfig entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cấu hình với ID: " + id));
        mapper.updateEntity(entity, request);
        return mapper.toResponse(repository.save(entity));
    }



    @Override
    public MatrixConfigResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cấu hình với ID: " + id));
    }

    @Override
    @Transactional
    public MatrixConfigResponse updateStatus(Long id, StatusEnum status) {
        MatrixConfig config = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cấu hình với ID: " + id));

        if (StatusEnum.ACTIVE.equals(status)) {
            // Vô hiệu hóa các cấu hình ACTIVE khác
            repository.findAll().stream()
                    .filter(c -> StatusEnum.ACTIVE.equals(c.getStatus()) && !c.getId().equals(id))
                    .forEach(c -> {
                        c.setStatus(StatusEnum.INACTIVE);
                        repository.save(c);
                    });
        }

        config.setStatus(status);
        return mapper.toResponse(repository.save(config));
    }

    @Override
    public List<MatrixConfigResponse> getAllByStatus(StatusEnum status) {
        if (status == null) {
            return mapper.toResponseList(repository.findAll());
        }
        return mapper.toResponseList(repository.findByStatus(status));
    }


}
