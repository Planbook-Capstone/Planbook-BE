package com.BE.service.interfaceServices;

import com.BE.enums.GenderEnum;
import com.BE.enums.RoleEnum;
import com.BE.enums.StatusEnum;
import com.BE.model.request.CreateUserRequest;
import com.BE.model.request.UserProfileRequest;
import com.BE.model.response.AuthenticationResponse;
import com.BE.model.response.UserResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface IUserService {

    UserResponse update(UUID id, UserProfileRequest request);

    UserResponse create(CreateUserRequest request);

    UserResponse updateStatus(UUID id, StatusEnum status);

    Page<UserResponse> getUsersWithFilter(String search, RoleEnum role, StatusEnum status, GenderEnum gender,
                                          int offset, int pageSize,
                                          String sortBy, String sortDirection);


}
