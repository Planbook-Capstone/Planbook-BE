package com.BE.service.interfaceServices;

import com.BE.model.request.BookTypeRequest;
import com.BE.model.response.BookTypeResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface IBookTypeService {
    BookTypeResponse createBookType(BookTypeRequest request);
    Page<BookTypeResponse> getAllBookTypes(int page, int size, String search, String status, String sortBy, String sortDirection);
    BookTypeResponse getBookTypeById(UUID id); // Quan trọng: Tham số là UUID
    BookTypeResponse updateBookType(UUID id, BookTypeRequest request); // Quan trọng: Tham số là UUID
    BookTypeResponse changeBookTypeStatus(UUID id, String newStatus); // Quan trọng: Tham số là UUID
}
