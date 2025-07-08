package com.BE.repository;


import com.BE.model.entity.ExternalToolConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalToolConfigRepository extends JpaRepository<ExternalToolConfig, Long> {
}

