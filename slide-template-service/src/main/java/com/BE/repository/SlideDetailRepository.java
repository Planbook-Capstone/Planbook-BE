package com.BE.repository;

import com.BE.model.entity.SlideDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlideDetailRepository extends JpaRepository<SlideDetail, String>, JpaSpecificationExecutor<SlideDetail> {
    List<SlideDetail> findBySlideTemplateId(Long slideTemplateId);
    void deleteBySlideTemplateId(Long slideTemplateId);
}
