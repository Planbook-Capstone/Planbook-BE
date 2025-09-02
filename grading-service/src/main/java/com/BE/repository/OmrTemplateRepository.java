package com.BE.repository;

import com.BE.model.entity.OmrTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OmrTemplateRepository extends JpaRepository<OmrTemplate, Long>, JpaSpecificationExecutor<OmrTemplate> {
}

