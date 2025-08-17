package com.BE.service.implementServices;

import com.BE.enums.ToolResultStatus;
import com.BE.exception.exceptions.BadRequestException;
import com.BE.mapper.ToolResultMapper;
import com.BE.model.entity.ToolResult;
import com.BE.model.request.CreateToolResultRequest;
import com.BE.model.request.ToolResultFilterRequest;
import com.BE.model.request.UpdateToolResultRequest;
import com.BE.model.response.ToolResultResponse;
import com.BE.repository.ToolResultRepository;
import com.BE.service.interfaceServices.IToolResultService;
import com.BE.specification.ToolResultSpecification;
import com.BE.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implementation của IToolResultService với Specification-based filtering
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ToolResultServiceImpl implements IToolResultService {

    private final ToolResultRepository toolResultRepository;
    private final ToolResultMapper toolResultMapper;
    private final PageUtil pageUtil;
    private static final int MAX_ARCHIVED_RESULTS = 30;


    @Override
    public ToolResultResponse create(CreateToolResultRequest request) {
        log.info("Tạo mới ToolResult với userId: {}, workspaceId: {}, type: {}",
                request.getUserId(), request.getAcademicYearId(), request.getType());

        try {
            ToolResult entity = toolResultMapper.toEntity(request);

            if(ToolResultStatus.ARCHIVED.equals(request.getStatus())){
                validateArchivedToolResultLimit(request.getUserId());
            }
            ToolResult savedEntity = toolResultRepository.save(entity);

            log.info("Tạo ToolResult thành công với id: {}", savedEntity.getId());
            return toolResultMapper.toResponse(savedEntity);
        } catch (Exception e) {
            log.error("Lỗi khi tạo ToolResult: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể tạo ToolResult: " + e.getMessage());
        }
    }


    private void validateArchivedToolResultLimit(UUID userId) {
        List<ToolResult> archivedResults = toolResultRepository.findByUserIdAndStatus(userId, ToolResultStatus.ARCHIVED);
        if (archivedResults.size() >= MAX_ARCHIVED_RESULTS) {
            throw new BadRequestException("Chỉ được lưu trữ tối đa 30 kết quả");
        }
    }


    @Override
    public ToolResultResponse update(Long id, UpdateToolResultRequest request) {
        log.info("Cập nhật ToolResult với id: {}", id);

        ToolResult existingEntity = toolResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ToolResult với id: " + id));

        try {
            toolResultMapper.updateEntityFromRequest(request, existingEntity);

            if(ToolResultStatus.ARCHIVED.equals(request.getStatus())){
                validateArchivedToolResultLimit(existingEntity.getUserId());
            }

            ToolResult updatedEntity = toolResultRepository.save(existingEntity);

            log.info("Cập nhật ToolResult thành công với id: {}", id);
            return toolResultMapper.toResponse(updatedEntity);
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật ToolResult với id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Không thể cập nhật ToolResult: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ToolResultResponse getById(Long id) {
        log.info("Lấy ToolResult theo id: {}", id);

        ToolResult entity = toolResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ToolResult với id: " + id));

        return toolResultMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ToolResultResponse> getAllWithFilter(ToolResultFilterRequest filterRequest) {
        log.info("Lấy danh sách ToolResult với filter: {}", filterRequest);

        try {
            // Validate và tạo Pageable từ PageUtil
            pageUtil.checkOffset(filterRequest.getPage());
            Pageable pageable = pageUtil.getPageable(
                    filterRequest.getPage() - 1, // Convert từ 1-based sang 0-based
                    filterRequest.getSize(),
                    filterRequest.getSortBy().getFieldName(), // Convert enum to string
                    filterRequest.getSortDirection().name().toLowerCase() // Convert enum to string
            );

            // Build Specification từ filter request
            Specification<ToolResult> specification = ToolResultSpecification.buildSpecification(
                    filterRequest.getUserId(),
                    filterRequest.getAcademicYearId(),
                    filterRequest.getType(),
                    filterRequest.getStatus(),
                    filterRequest.getSource(),
                    filterRequest.getTemplateId(),
                    filterRequest.getNameContains(),
                    filterRequest.getDescriptionContains(),
                    filterRequest.getLessonIds()
            );

            // Query với Specification
            Page<ToolResult> entities = toolResultRepository.findAll(specification, pageable);

            log.info("Tìm thấy {} ToolResult với filter", entities.getTotalElements());
            return entities.map(toolResultMapper::toResponse);

        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách ToolResult với filter: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể lấy danh sách ToolResult: " + e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        log.info("Xóa mềm ToolResult với id: {}", id);

        ToolResult existingEntity = toolResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ToolResult với id: " + id));

        try {
            // Soft delete: chỉ update status thành DELETED
            existingEntity.setStatus(com.BE.enums.ToolResultStatus.DELETED);
            toolResultRepository.save(existingEntity);

            log.info("Xóa mềm ToolResult thành công với id: {}", id);
        } catch (Exception e) {
            log.error("Lỗi khi xóa mềm ToolResult với id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Không thể xóa ToolResult: " + e.getMessage());
        }
    }

    @Override
    public ToolResultResponse updateStatus(Long id, ToolResultStatus status) {
        ToolResult existingEntity = toolResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ToolResult với id: " + id));
        existingEntity.setStatus(status);
        return toolResultMapper.toResponse(toolResultRepository.save(existingEntity));
    }


}
