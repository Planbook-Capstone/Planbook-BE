package com.BE.repository;

import com.BE.model.entity.StudentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentSubmissionRepository extends JpaRepository<StudentSubmission, Long> {
    java.util.List<StudentSubmission> findByGradingSessionId(Long gradingSessionId);

    Optional<StudentSubmission>  findByGradingSessionIdAndStudentCode(Long id, String code);
}

