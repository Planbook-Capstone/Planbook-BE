package com.BE.mapper;
import com.BE.model.entity.SubscriptionPackage;
import com.BE.model.request.SubscriptionPackageRequest;
import com.BE.model.response.SubscriptionPackageResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SubscriptionPackageMapper {

    SubscriptionPackage toEntity(SubscriptionPackageRequest request);

    SubscriptionPackageResponse toResponse(SubscriptionPackage entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(SubscriptionPackageRequest request, @MappingTarget SubscriptionPackage entity);
}
