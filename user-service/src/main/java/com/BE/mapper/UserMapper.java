package com.BE.mapper;

import com.BE.model.entity.UserProfile;
import com.BE.model.request.UserProfileRequest;
import com.BE.model.response.UserProfileResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserProfileResponse toResponse(UserProfile userProfile);

    UserProfile toEntity(UserProfileRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget UserProfile userProfile, UserProfileRequest request);

}
