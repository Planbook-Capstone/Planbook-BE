package com.BE.repository;

import com.BE.model.entity.ExamInstance;
import com.BE.enums.ExamInstanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamInstanceRepository extends JpaRepository<ExamInstance, UUID> {
    
    Optional<ExamInstance> findByCode(String code);
    
    @Query("SELECT ei FROM ExamInstance ei WHERE ei.template.createdBy = :teacherId ORDER BY ei.createdAt DESC")
    List<ExamInstance> findByTeacherIdOrderByCreatedAtDesc(@Param("teacherId") UUID teacherId);

    boolean existsByCode(String code);

    // Methods for scheduler service
    List<ExamInstance> findByStatusAndStartAtBetween(ExamInstanceStatus status, LocalDateTime startTime, LocalDateTime endTime);

    List<ExamInstance> findByStatusAndEndAtBetween(ExamInstanceStatus status, LocalDateTime startTime, LocalDateTime endTime);
}
