package com.BE.mapper;

import com.BE.model.entity.User;
import com.BE.model.request.AuthenticationRequest;
import com.BE.model.response.AuthenticationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    User toAuth(AuthenticationRequest request);

    AuthenticationResponse toAuthenticationResponse(User user);

    void updateAuthUser(@MappingTarget User user, AuthenticationRequest request);

}
