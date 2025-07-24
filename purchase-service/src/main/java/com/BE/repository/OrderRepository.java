package com.BE.repository;

import com.BE.enums.StatusEnum;
import com.BE.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> , JpaSpecificationExecutor<Order> {

    boolean existsByUserIdAndSubscriptionPackageIdAndStatusIn(UUID userId, UUID packageId, List<StatusEnum> statuses);

}
