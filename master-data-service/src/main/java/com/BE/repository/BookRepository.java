package com.BE.repository;

import com.BE.enums.StatusEnum;
import com.BE.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book,Long> {

    // Tìm kiếm sách theo tên (case-insensitive) trong cùng một Subject
    Optional<Book> findByNameAndSubjectId(String name, Long subjectId);

    // Kiểm tra tên sách đã tồn tại trong cùng một Subject và khác ID
    Optional<Book> findByNameAndSubjectIdAndIdNot(String name, Long subjectId, Long id);

    // Lấy tất cả sách có phân trang, tìm kiếm, lọc theo trạng thái
    @Query("SELECT b FROM Book b WHERE " +
            "(:search IS NULL OR b.name LIKE %:search%) AND " +
            "(:status IS NULL OR b.status = :status)")
    Page<Book> findAllBooks(
            @Param("search") String search,
            @Param("status") StatusEnum status,
            Pageable pageable);

    // Lấy tất cả sách theo Subject ID, có phân trang, tìm kiếm, lọc theo trạng thái
    @Query("SELECT b FROM Book b WHERE b.subject.id = :subjectId AND " +
            "(:search IS NULL OR b.name LIKE %:search%) AND " +
            "(:status IS NULL OR b.status = :status)")
    Page<Book> findBySubjectId(
            @Param("subjectId") Long subjectId,
            @Param("search") String search,
            @Param("status") StatusEnum status,
            Pageable pageable);
}
