package com.BE.service.interfaceServices;

import com.BE.enums.StatusEnum;
import com.BE.model.request.SubscriptionPackageRequest;
import com.BE.model.response.SubscriptionPackageResponse;

import java.util.List;
import java.util.UUID;

public interface ISubscriptionPackageService {

    SubscriptionPackageResponse create(SubscriptionPackageRequest request);
    List<SubscriptionPackageResponse> getAll(StatusEnum status, String sortBy, String sortDirection);
    SubscriptionPackageResponse changeStatus(UUID id);
    SubscriptionPackageResponse getById(UUID id);
    void delete(UUID id);

    SubscriptionPackageResponse update(UUID id, SubscriptionPackageRequest request);

}
