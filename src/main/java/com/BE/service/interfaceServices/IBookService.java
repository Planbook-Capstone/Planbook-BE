package com.BE.service.interfaceServices;

import com.BE.model.request.BookRequest;
import com.BE.model.response.BookResponse;
import org.springframework.data.domain.Page;

public interface IBookService {
    BookResponse createBook(BookRequest request);
    Page<BookResponse> getAllBooks(int page, int size, String search, String status, String sortBy, String sortDirection);
    BookResponse getBookById(long id);
    BookResponse updateBook(long id, BookRequest request);
    BookResponse changeBookStatus(long id, String newStatus);

    // API mới: Lấy danh sách Book theo Subject ID
    Page<BookResponse> getBooksBySubjectId(Long subjectId, int page, int size, String search, String status, String sortBy, String sortDirection);
}
