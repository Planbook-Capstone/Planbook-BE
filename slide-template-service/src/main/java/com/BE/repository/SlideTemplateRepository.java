package com.BE.repository;

import com.BE.model.entity.SlideTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SlideTemplateRepository extends JpaRepository<SlideTemplate, Long>, JpaSpecificationExecutor<SlideTemplate> {
}
