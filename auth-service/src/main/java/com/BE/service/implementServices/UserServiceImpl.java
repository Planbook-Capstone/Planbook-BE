package com.BE.service.implementServices;

import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.UserMapper;
import com.BE.model.entity.User;
import com.BE.model.request.UserProfileRequest;
import com.BE.model.response.UserResponse;
import com.BE.repository.AuthenRepository;
import com.BE.service.interfaceServices.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserServiceImpl implements IUserService {

    AuthenRepository repository;
    UserMapper mapper;


    @Override
    public UserResponse update(UUID id, UserProfileRequest request) {
        User existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hồ sơ người dùng để cập nhật."));
        mapper.update(existing, request);
        return mapper.toResponse(repository.save(existing));
    }
}
