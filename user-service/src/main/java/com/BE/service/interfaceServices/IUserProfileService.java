package com.BE.service.interfaceServices;

import com.BE.model.request.UserProfileRequest;
import com.BE.model.response.UserProfileResponse;

import java.util.UUID;

public interface IUserProfileService {
    UserProfileResponse getById(UUID id);

    UserProfileResponse create(UUID id, UserProfileRequest request); // id từ auth-service

    UserProfileResponse update(UUID id, UserProfileRequest request);

    void delete(UUID id);
}
