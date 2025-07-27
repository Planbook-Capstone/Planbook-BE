package com.BE.service.implementServices;

import com.BE.enums.StatusEnum;
import com.BE.exception.exceptions.BadRequestException;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.SlideTemplateMapper;
import com.BE.model.entity.SlideTemplate;
import com.BE.model.request.SlideTemplateRequest;
import com.BE.model.response.SlideTemplateResponse;
import com.BE.repository.SlideTemplateRepository;
import com.BE.service.interfaceServices.ISlideTemplateService;
import com.BE.service.interfaceServices.ISlideDetailService;
import com.BE.utils.DateNowUtils;
import com.BE.utils.PageUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SlideTemplateServiceImpl implements ISlideTemplateService {

    PageUtil pageUtil;
    SlideTemplateRepository slideTemplateRepository;
    SlideTemplateMapper slideTemplateMapper;
    ISlideDetailService iSlideDetailService;
    ObjectMapper objectMapper;

    @Override
    @Transactional
    public SlideTemplate saveSlideTemplate(SlideTemplateRequest request) {
        SlideTemplate entity = slideTemplateMapper.toEntity(request);
        entity.setStatus(StatusEnum.ACTIVE);
        SlideTemplate savedEntity = slideTemplateRepository.save(entity);

        // Xử lý JSON tổng để tạo SlideDetail
        String slideDataJson = extractSlideDataJson(request);
        if (slideDataJson != null && !slideDataJson.trim().isEmpty()) {
            iSlideDetailService.processSlideDetailsFromTemplate(savedEntity.getId(), slideDataJson);
        }

        return savedEntity;
    }

    @Override
    public SlideTemplateResponse getSlideTemplate(Long id) {
        SlideTemplate form = slideTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biểu mẫu"));
        return slideTemplateMapper.toResponse(form);
    }

    @Override
    public Page<SlideTemplateResponse> getAllSlideTemplates(
            int page, int size, String search, String status, String sortBy, String sortDirection) {

        pageUtil.checkOffset(page);
        Pageable pageable = pageUtil.getPageable(page - 1, size, sortBy, sortDirection);

        Specification<SlideTemplate> spec = Specification.where(null);

        if (search != null && !search.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"));
        }

        if (status != null) {
            try {
                StatusEnum statusEnum = StatusEnum.valueOf(status.toUpperCase());
                spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), statusEnum));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Trạng thái không hợp lệ: " + status);
            }
        }

        Page<SlideTemplate> pageResult = slideTemplateRepository.findAll(spec, pageable);
        return pageResult.map(slideTemplateMapper::toResponse);
    }

    @Override
    @Transactional
    public SlideTemplateResponse updateSlideTemplate(Long id, SlideTemplateRequest request) {
        SlideTemplate existingForm = slideTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biểu mẫu có id: " + id));

        slideTemplateMapper.updateEntityFromRequest(request, existingForm);

        SlideTemplate updated = slideTemplateRepository.save(existingForm);

        // Xử lý JSON tổng để cập nhật SlideDetail
        String slideDataJson = extractSlideDataJson(request);
        if (slideDataJson != null && !slideDataJson.trim().isEmpty()) {
            iSlideDetailService.processSlideDetailsFromTemplate(updated.getId(), slideDataJson);
        }

        return slideTemplateMapper.toResponse(updated);
    }

    @Override
    public SlideTemplateResponse changeSlideTemplateStatus(long id, String newStatus) {
        SlideTemplate template = slideTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biểu mẫu"));

        StatusEnum statusEnum;
        try {
            statusEnum = StatusEnum.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status value. Must be 'ACTIVE' or 'INACTIVE'.");
        }

        template.setStatus(statusEnum);
        SlideTemplate updated = slideTemplateRepository.save(template);
        return slideTemplateMapper.toResponse(updated);
    }

    /**
     * Trích xuất JSON tổng từ request
     * Ưu tiên slideDataJson, nếu không có thì kiểm tra textBlocks có chứa JSON tổng
     * không
     */
    private String extractSlideDataJson(SlideTemplateRequest request) {
        // Ưu tiên slideDataJson nếu có
        if (request.getSlideDataJson() != null && !request.getSlideDataJson().trim().isEmpty()) {
            return request.getSlideDataJson();
        }

        // Kiểm tra textBlocks có chứa JSON tổng không
        if (request.getTextBlocks() != null) {
            try {
                // Chuyển textBlocks thành JSON string để kiểm tra
                String textBlocksJson = objectMapper.writeValueAsString(request.getTextBlocks());

                // Kiểm tra xem có chứa các key đặc trưng của JSON tổng không
                if (textBlocksJson.contains("\"version\"") &&
                        textBlocksJson.contains("\"slides\"") &&
                        textBlocksJson.contains("\"slideFormat\"")) {
                    return textBlocksJson;
                }
            } catch (Exception e) {
                // Ignore error, không phải JSON tổng
            }
        }

        return null;
    }

}
