package com.BE.mapper;

import com.BE.model.request.AuthenticationRequest;
import com.BE.model.request.RegisterAggregatorRequest;
import com.BE.model.request.UserProfileRequest;
import com.BE.model.response.AuthenticationResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AggregatorMapper {
    AuthenticationRequest toAuthenticationRequest(RegisterAggregatorRequest req);
    UserProfileRequest toUserProfileRequest(RegisterAggregatorRequest req);




}
