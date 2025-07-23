package com.BE.mapper;

import com.BE.model.entity.User;
import com.BE.model.request.UserProfileRequest;
import com.BE.model.response.UserResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget User user, UserProfileRequest request);

    UserResponse toResponse(User user);

}
