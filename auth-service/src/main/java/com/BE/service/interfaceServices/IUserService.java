package com.BE.service.interfaceServices;

import com.BE.model.request.UserProfileRequest;
import com.BE.model.response.AuthenticationResponse;
import com.BE.model.response.UserResponse;

import java.util.UUID;

public interface IUserService {

    UserResponse update(UUID id, UserProfileRequest request);
}
