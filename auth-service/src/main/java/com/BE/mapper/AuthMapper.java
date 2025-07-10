package com.BE.mapper;

import com.BE.model.entity.AuthUser;
import com.BE.model.request.AuthenticationRequest;
import com.BE.model.response.AuthenticationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    AuthUser toAuth(AuthenticationRequest request);

    AuthenticationResponse toAuthenticationResponse(AuthUser user);

    void updateAuthUser(@MappingTarget AuthUser user, AuthenticationRequest request);

}
