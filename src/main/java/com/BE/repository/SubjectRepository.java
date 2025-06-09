package com.BE.repository;

import com.BE.enums.StatusEnum;
import com.BE.model.entity.Subject;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    // Tìm kiếm môn học theo tên (case-insensitive)
    Optional<Subject> findByName(String name);

    // Tìm kiếm môn học theo tên (case-insensitive) và ID khác (dùng khi cập nhật)
    Optional<Subject> findByNameAndIdNot(String name, Long id);

    // Lấy tất cả môn học có phân trang, tìm kiếm, lọc theo trạng thái
    @Query("SELECT s FROM Subject s WHERE " +
            "(:search IS NULL OR s.name LIKE %:search%) AND " +
            "(:status IS NULL OR s.status = :status)")
    Page<Subject> findAllSubjects(
            @Param("search") String search,
            @Param("status") StatusEnum status,
            Pageable pageable);

    // Lấy tất cả môn học theo Grade ID, có phân trang, tìm kiếm, lọc theo trạng thái
    @Query("SELECT s FROM Subject s WHERE s.grade.id = :gradeId AND " +
            "(:search IS NULL OR s.name LIKE %:search%) AND " +
            "(:status IS NULL OR s.status = :status)")
    Page<Subject> findByGradeId(
            @Param("gradeId") Long gradeId,
            @Param("search") String search,
            @Param("status") StatusEnum status,
            Pageable pageable);

    // Kiểm tra tồn tại môn học theo tên và gradeId
    Optional<Subject> findByNameAndGradeId(String name, Long gradeId);

    // Kiểm tra tồn tại môn học theo tên, gradeId và ID khác (khi update)
    Optional<Subject> findByNameAndGradeIdAndIdNot(String name, Long gradeId, Long subjectId);
}
