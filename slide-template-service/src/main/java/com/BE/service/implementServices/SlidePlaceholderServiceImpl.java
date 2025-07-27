package com.BE.service.implementServices;

import com.BE.enums.StatusEnum;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.SlidePlaceholderMapper;
import com.BE.model.entity.SlidePlaceholder;
import com.BE.model.request.SlidePlaceholderRequest;
import com.BE.model.response.SlidePlaceholderResponse;
import com.BE.repository.SlidePlaceholderRepository;
import com.BE.service.interfaceServices.ISlidePlaceholderService;
import com.BE.utils.PageUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SlidePlaceholderServiceImpl implements ISlidePlaceholderService {

    PageUtil pageUtil;
    SlidePlaceholderRepository slidePlaceholderRepository;
    SlidePlaceholderMapper slidePlaceholderMapper;

    @Override
    public SlidePlaceholder saveSlidePlaceholder(SlidePlaceholderRequest request) {
        SlidePlaceholder entity = slidePlaceholderMapper.toEntity(request);
        entity.setStatus(StatusEnum.ACTIVE);
        return slidePlaceholderRepository.save(entity);
    }

    @Override
    public SlidePlaceholderResponse getSlidePlaceholder(Long id) {
        SlidePlaceholder placeholder = slidePlaceholderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy placeholder với id: " + id));
        return slidePlaceholderMapper.toResponse(placeholder);
    }

    @Override
    public List<SlidePlaceholderResponse> getAllSlidePlaceholders() {
        List<SlidePlaceholder> placeholders = slidePlaceholderRepository.findAll();
        return placeholders.stream()
                .map(slidePlaceholderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<SlidePlaceholderResponse> getAllSlidePlaceholders(
            int page, int size, String search, String status, String sortBy, String sortDirection) {

        pageUtil.checkOffset(page);
        Pageable pageable = pageUtil.getPageable(page - 1, size, sortBy, sortDirection);

        Specification<SlidePlaceholder> spec = Specification.where(null);

        if (search != null && !search.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + search.toLowerCase() + "%")
            ));
        }

        if (status != null && !status.isBlank()) {
            try {
                StatusEnum statusEnum = StatusEnum.valueOf(status.toUpperCase());
                spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), statusEnum));
            } catch (IllegalArgumentException e) {
                // Ignore invalid status
            }
        }

        Page<SlidePlaceholder> placeholderPage = slidePlaceholderRepository.findAll(spec, pageable);
        return placeholderPage.map(slidePlaceholderMapper::toResponse);
    }

    @Override
    public SlidePlaceholderResponse updateSlidePlaceholder(Long id, SlidePlaceholderRequest request) {
        SlidePlaceholder existingPlaceholder = slidePlaceholderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy placeholder với id: " + id));

        slidePlaceholderMapper.updateEntityFromRequest(request, existingPlaceholder);

        SlidePlaceholder updated = slidePlaceholderRepository.save(existingPlaceholder);
        return slidePlaceholderMapper.toResponse(updated);
    }

    @Override
    public SlidePlaceholderResponse changeSlidePlaceholderStatus(long id, String newStatus) {
        SlidePlaceholder placeholder = slidePlaceholderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy placeholder với id: " + id));

        try {
            StatusEnum statusEnum = StatusEnum.valueOf(newStatus.toUpperCase());
            placeholder.setStatus(statusEnum);
            SlidePlaceholder updated = slidePlaceholderRepository.save(placeholder);
            return slidePlaceholderMapper.toResponse(updated);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ: " + newStatus);
        }
    }
}
