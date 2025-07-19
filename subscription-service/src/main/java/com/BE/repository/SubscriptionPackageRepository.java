package com.BE.repository;

import com.BE.model.entity.SubscriptionPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubscriptionPackageRepository extends JpaRepository<SubscriptionPackage, UUID>, JpaSpecificationExecutor<SubscriptionPackage> {
    boolean existsByName(String name);
}
