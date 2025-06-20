package com.BE.repository;

import com.BE.enums.StatusEnum;
import com.BE.model.entity.Grade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {


    Optional<Grade> findByName(String name);

    // Tìm kiếm phân trang các khối lớp theo tên (không phân biệt chữ hoa, chữ thường)
    Page<Grade> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Tìm kiếm phân trang các khối lớp theo trạng thái
    Page<Grade> findByStatus(StatusEnum status, Pageable pageable);

    // Tìm kiếm phân trang các khối lớp theo tên VÀ trạng thái
    Page<Grade> findByNameContainingIgnoreCaseAndStatus(String name, StatusEnum status, Pageable pageable);
}
