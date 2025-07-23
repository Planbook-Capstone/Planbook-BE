package com.BE.repository;

import com.BE.enums.StatusEnum;
import com.BE.model.entity.BookType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface BookTypeRepository extends JpaRepository<BookType, UUID> {

    // Tìm kiếm BookType theo tên (case-insensitive)
    Optional<BookType> findByNameIgnoreCase(String name);

    // Kiểm tra tên BookType đã tồn tại và khác ID (để cập nhật)
    Optional<BookType> findByNameIgnoreCaseAndIdNot(String name, UUID id); // Quan trọng: ID là UUID

    // Lấy tất cả BookType có phân trang, tìm kiếm, lọc theo trạng thái
    @Query("SELECT bt FROM BookType bt WHERE " +
            "(:search IS NULL OR LOWER(bt.name) LIKE %:search%) AND " + // LOWER để tìm kiếm không phân biệt hoa thường
            "(:status IS NULL OR bt.status = :status)")
    Page<BookType> findAllBookTypes(
            @Param("search") String search,
            @Param("status") StatusEnum status,
            Pageable pageable);
}
