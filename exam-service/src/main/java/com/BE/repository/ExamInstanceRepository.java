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
    
    @Query("SELECT ei FROM ExamInstance ei WHERE ei.code = :code AND ei.startAt <= :now AND ei.endAt >= :now")
    Optional<ExamInstance> findActiveExamByCode(@Param("code") String code, @Param("now") LocalDateTime now);

    @Query("SELECT ei FROM ExamInstance ei WHERE ei.code = :code AND ei.status = :status")
    Optional<ExamInstance> findByCodeAndStatus(@Param("code") String code, @Param("status") ExamInstanceStatus status);

    @Query("SELECT ei FROM ExamInstance ei WHERE ei.template.createdBy = :teacherId AND ei.status = :status ORDER BY ei.createdAt DESC")
    List<ExamInstance> findByTeacherIdAndStatusOrderByCreatedAtDesc(@Param("teacherId") UUID teacherId, @Param("status") ExamInstanceStatus status);

    boolean existsByCode(String code);
}
