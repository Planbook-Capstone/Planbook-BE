package com.BE.service.implementServices;

import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.UserMapper;
import com.BE.model.entity.UserProfile;
import com.BE.model.request.UserProfileRequest;
import com.BE.model.response.UserProfileResponse;
import com.BE.repository.UserProfileRepository;
import com.BE.service.interfaceServices.IUserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserProfileServiceImpl implements IUserProfileService {

    UserProfileRepository repository;
    UserMapper mapper;

    @Override
    public UserProfileResponse getById(UUID id) {
        UserProfile profile = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hồ sơ người dùng với ID đã cho."));
        return mapper.toResponse(profile);
    }

    @Override
    public UserProfileResponse create(UUID id, UserProfileRequest request) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("ID người dùng không được để trống");
            }

            if (repository.existsById(id)) {
                throw new IllegalStateException("Hồ sơ người dùng đã tồn tại");
            }

            UserProfile profile = mapper.toEntity(request);
            profile.setId(id);
            profile.setCreatedAt(LocalDateTime.now());
            profile.setUpdatedAt(LocalDateTime.now());

            return mapper.toResponse(repository.save(profile));
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e; // sẽ được xử lý ở tầng ControllerAdvice (nếu có)
        } catch (Exception e) {
            throw new RuntimeException("Đã xảy ra lỗi khi tạo hồ sơ người dùng", e);
        }
    }

    @Override
    public UserProfileResponse update(UUID id, UserProfileRequest request) {
        UserProfile existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hồ sơ người dùng để cập nhật."));
        mapper.update(existing, request);
        existing.setUpdatedAt(LocalDateTime.now());
        return mapper.toResponse(repository.save(existing));
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Không tìm thấy hồ sơ người dùng để xóa.");
        }
        repository.deleteById(id);
    }
}
