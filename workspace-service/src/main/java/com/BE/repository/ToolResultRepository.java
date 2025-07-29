package com.BE.repository;

import com.BE.model.entity.ToolResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository interface cho ToolResult entity với JpaSpecificationExecutor
 * để hỗ trợ dynamic queries linh hoạt
 */
@Repository
public interface ToolResultRepository extends JpaRepository<ToolResult, Long>, JpaSpecificationExecutor<ToolResult> {

    // JpaSpecificationExecutor cung cấp các methods:
    // - findAll(Specification<T> spec)
    // - findAll(Specification<T> spec, Pageable pageable)
    // - findAll(Specification<T> spec, Sort sort)
    // - findOne(Specification<T> spec)
    // - count(Specification<T> spec)
    // - exists(Specification<T> spec)

    // Không cần định nghĩa thêm methods vì JpaSpecificationExecutor
    // đã cung cấp đầy đủ functionality cho dynamic queries
}
