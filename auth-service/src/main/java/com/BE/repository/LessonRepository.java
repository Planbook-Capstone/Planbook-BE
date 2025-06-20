package com.BE.repository;

import com.BE.enums.StatusEnum;
import com.BE.model.entity.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson,Long> {

    // Tìm kiếm bài học theo tên (case-insensitive) trong cùng một Chapter
    Optional<Lesson> findByNameAndChapterId(String name, Long chapterId);

    // Kiểm tra tên bài học đã tồn tại trong cùng một Chapter và khác ID
    Optional<Lesson> findByNameAndChapterIdAndIdNot(String name, Long chapterId, Long id);

    // Lấy tất cả bài học có phân trang, tìm kiếm, lọc theo trạng thái
    @Query("SELECT l FROM Lesson l WHERE " +
            "(:search IS NULL OR l.name LIKE %:search%) AND " +
            "(:status IS NULL OR l.status = :status)")
    Page<Lesson> findAllLessons(
            @Param("search") String search,
            @Param("status") StatusEnum status,
            Pageable pageable);

    // Lấy tất cả bài học theo Chapter ID, có phân trang, tìm kiếm, lọc theo trạng thái
    @Query("SELECT l FROM Lesson l WHERE l.chapter.id = :chapterId AND " +
            "(:search IS NULL OR l.name LIKE %:search%) AND " +
            "(:status IS NULL OR l.status = :status)")
    Page<Lesson> findByChapterId(
            @Param("chapterId") Long chapterId,
            @Param("search") String search,
            @Param("status") StatusEnum status,
            Pageable pageable);
}
