package com.BE.repository;

import com.BE.enums.StatusEnum;
import com.BE.model.entity.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    // Tìm kiếm chương theo tên (case-insensitive) trong cùng một Book
    Optional<Chapter> findByNameAndBookId(String name, Long bookId);

    // Kiểm tra tên chương đã tồn tại trong cùng một Book và khác ID
    Optional<Chapter> findByNameAndBookIdAndIdNot(String name, Long bookId, Long id);

    // Lấy tất cả chương có phân trang, tìm kiếm, lọc theo trạng thái
    @Query("SELECT c FROM Chapter c WHERE " +
            "(:search IS NULL OR c.name LIKE %:search%) AND " +
            "(:status IS NULL OR c.status = :status)")
    Page<Chapter> findAllChapters(
            @Param("search") String search,
            @Param("status") StatusEnum status,
            Pageable pageable);

    // Lấy tất cả chương theo Book ID, có phân trang, tìm kiếm, lọc theo trạng thái
    @Query("SELECT c FROM Chapter c WHERE c.book.id = :bookId AND " +
            "(:search IS NULL OR c.name LIKE %:search%) AND " +
            "(:status IS NULL OR c.status = :status)")
    Page<Chapter> findByBookId(
            @Param("bookId") Long bookId,
            @Param("search") String search,
            @Param("status") StatusEnum status,
            Pageable pageable);
}
