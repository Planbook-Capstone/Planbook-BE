package com.BE.repository;

import com.BE.model.entity.StudentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentSubmissionRepository extends JpaRepository<StudentSubmission, Long> {
    java.util.List<StudentSubmission> findByGradingSessionId(Long gradingSessionId);
}

