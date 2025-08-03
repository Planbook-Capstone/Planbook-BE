package com.BE.repository;

import com.BE.model.entity.ExamTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExamTemplateRepository extends JpaRepository<ExamTemplate, UUID> {
    
    @Query("SELECT et FROM ExamTemplate et WHERE et.createdBy = :createdBy ORDER BY et.createdAt DESC")
    List<ExamTemplate> findByCreatedByOrderByCreatedAtDesc(@Param("createdBy") UUID createdBy);

}
