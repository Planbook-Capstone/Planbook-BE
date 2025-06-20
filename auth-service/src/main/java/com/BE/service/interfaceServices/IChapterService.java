package com.BE.service.interfaceServices;

import com.BE.model.request.ChapterRequest;
import com.BE.model.response.ChapterResponse;
import org.springframework.data.domain.Page;

public interface IChapterService {

    ChapterResponse createChapter(ChapterRequest request);
    Page<ChapterResponse> getAllChapters(int page, int size, String search, String status, String sortBy, String sortDirection);
    ChapterResponse getChapterById(long id);
    ChapterResponse updateChapter(long id, ChapterRequest request);
    ChapterResponse changeChapterStatus(long id, String newStatus);

    // API mới: Lấy danh sách Chapter theo Book ID
    Page<ChapterResponse> getChaptersByBookId(Long bookId, int page, int size, String search, String status, String sortBy, String sortDirection);

}
