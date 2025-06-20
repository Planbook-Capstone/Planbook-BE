package com.BE.service.interfaceServices;

import com.BE.model.request.LessonRequest;
import com.BE.model.response.LessonResponse;
import org.springframework.data.domain.Page;

public interface ILessonService {

    LessonResponse createLesson(LessonRequest request);
    Page<LessonResponse> getAllLessons(int page, int size, String search, String status, String sortBy, String sortDirection);
    LessonResponse getLessonById(long id);
    LessonResponse updateLesson(long id, LessonRequest request);
    LessonResponse changeLessonStatus(long id, String newStatus);

    // API mới: Lấy danh sách Lesson theo Chapter ID
    Page<LessonResponse> getLessonsByChapterId(Long chapterId, int page, int size, String search, String status, String sortBy, String sortDirection);
}
