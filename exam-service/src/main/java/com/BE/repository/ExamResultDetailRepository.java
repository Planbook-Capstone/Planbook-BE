package com.BE.repository;

import com.BE.model.entity.ExamResultDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExamResultDetailRepository extends JpaRepository<ExamResultDetail, UUID> {
    @Modifying
    @Query("DELETE FROM ExamResultDetail erd WHERE erd.submission.id = :submissionId")
    void deleteBySubmissionId(@Param("submissionId") UUID submissionId);
}
