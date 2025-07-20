package com.BE.repository;

import com.BE.model.entity.ExamSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamSubmissionRepository extends JpaRepository<ExamSubmission, UUID> {

    @Query("SELECT es FROM ExamSubmission es WHERE es.examInstance.id = :examInstanceId ORDER BY es.submittedAt DESC")
    List<ExamSubmission> findByExamInstanceIdOrderBySubmittedAtDesc(@Param("examInstanceId") UUID examInstanceId);

    @Query("SELECT es FROM ExamSubmission es WHERE es.examInstance.code = :code AND es.studentName = :studentName")
    Optional<ExamSubmission> findByExamCodeAndStudentName(@Param("code") String code, @Param("studentName") String studentName);

    @Query("SELECT COUNT(es) FROM ExamSubmission es WHERE es.examInstance.id = :examInstanceId")
    Long countByExamInstanceId(@Param("examInstanceId") UUID examInstanceId);

    @Modifying
    @Query("DELETE FROM ExamSubmission es WHERE es.examInstance.id = :examInstanceId")
    void deleteByExamInstanceId(@Param("examInstanceId") UUID examInstanceId);
}
