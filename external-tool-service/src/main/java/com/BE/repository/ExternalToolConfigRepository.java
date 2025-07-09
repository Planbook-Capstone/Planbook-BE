package com.BE.repository;


import com.BE.enums.StatusEnum;
import com.BE.model.entity.ExternalToolConfig;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ExternalToolConfigRepository extends JpaRepository<ExternalToolConfig, Long>, JpaSpecificationExecutor<ExternalToolConfig> {
}

