package com.BE.service.implementServices;


import com.BE.enums.StatusEnum;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.BookMapper;
import com.BE.model.entity.Book;
import com.BE.model.entity.Subject;
import com.BE.model.request.BookRequest;
import com.BE.model.response.BookResponse;
import com.BE.repository.BookRepository;
import com.BE.repository.SubjectRepository;
import com.BE.service.interfaceServices.IBookService;
import com.BE.utils.DateNowUtils;
import com.BE.utils.PageUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookServiceImpl implements IBookService {

    @Autowired
    PageUtil pageUtil;

    @Autowired
    DateNowUtils dateNowUtils;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    BookMapper bookMapper;

    @Override
    public BookResponse createBook(BookRequest request) {
        // Kiểm tra tên sách đã tồn tại trong cùng một Subject chưa
        if (bookRepository.findByNameAndSubjectId(request.getName().trim(), request.getSubjectId()).isPresent()) {
            throw new DataIntegrityViolationException("Book with name '" + request.getName() + "' already exists for Subject ID: " + request.getSubjectId());
        }

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new NotFoundException("Subject not found with ID: " + request.getSubjectId()));

        Book book = bookMapper.toBook(request);
        book.setSubject(subject); // Set Subject entity
        book.setStatus(StatusEnum.ACTIVE); // Mặc định là ACTIVE khi tạo
        book.setCreatedAt(dateNowUtils.dateNow());
        book.setUpdatedAt(dateNowUtils.dateNow());

        Book savedBook = bookRepository.save(book);
        return bookMapper.toBookResponse(savedBook);
    }

    @Override
    public Page<BookResponse> getAllBooks(int page, int size, String search, String status, String sortBy, String sortDirection) {
//        pageUtil.checkOffset(page);
        Pageable pageable = pageUtil.getPageable(page, size, sortBy, sortDirection);

        StatusEnum statusEnum = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                statusEnum = StatusEnum.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status value: " + status + ". Must be ACTIVE or INACTIVE.");
            }
        }

        Page<Book> booksPage = bookRepository.findAllBooks(
                (search != null && !search.trim().isEmpty()) ? search : null,
                statusEnum,
                pageable
        );
        return booksPage.map(bookMapper::toBookResponse);
    }

    @Override
    public BookResponse getBookById(long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + id));
        return bookMapper.toBookResponse(book);
    }

    @Override
    public BookResponse updateBook(long id, BookRequest request) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + id));

        // Kiểm tra xem Subject ID có thay đổi không
        if (existingBook.getSubject().getId() != request.getSubjectId()) {
            Subject newSubject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new NotFoundException("New Subject not found with ID: " + request.getSubjectId()));
            existingBook.setSubject(newSubject);
        }

        // Kiểm tra tên sách có trùng với sách khác trong CÙNG subject không
        boolean nameChanged = !existingBook.getName().equalsIgnoreCase(request.getName());
        boolean subjectChanged = existingBook.getSubject().getId() != request.getSubjectId();

        if (nameChanged || subjectChanged) {
            Optional<Book> duplicateBook = bookRepository.findByNameAndSubjectIdAndIdNot(request.getName(), request.getSubjectId(), id);
            if (duplicateBook.isPresent()) {
                throw new DataIntegrityViolationException("Book with name '" + request.getName() + "' already exists for Subject ID: " + request.getSubjectId());
            }
        }

        existingBook.setName(request.getName());
        existingBook.setUpdatedAt(dateNowUtils.dateNow());

        try {
            Book updatedBook = bookRepository.save(existingBook);
            return bookMapper.toBookResponse(updatedBook);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Failed to update book: Book with name '" + request.getName() + "' already exists for Subject ID: " + request.getSubjectId() + " (Possible race condition).");
        }
    }

    @Override
    public BookResponse changeBookStatus(long id, String newStatus) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + id));

        StatusEnum statusEnum = null;
        try {
            statusEnum = StatusEnum.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + newStatus + ". Must be ACTIVE or INACTIVE.");
        }

        existingBook.setStatus(statusEnum);
        existingBook.setUpdatedAt(dateNowUtils.dateNow());

        Book updatedBook = bookRepository.save(existingBook);
        return bookMapper.toBookResponse(updatedBook);
    }

    @Override
    public Page<BookResponse> getBooksBySubjectId(Long subjectId, int page, int size, String search, String status, String sortBy, String sortDirection) {
//        pageUtil.checkOffset(page);
        Pageable pageable = pageUtil.getPageable(page, size, sortBy, sortDirection);

        // Kiểm tra sự tồn tại của Subject
        if (!subjectRepository.existsById(subjectId)) {
            throw new NotFoundException("Subject not found with ID: " + subjectId);
        }

        StatusEnum statusEnum = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                statusEnum = StatusEnum.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status value: " + status + ". Must be ACTIVE or INACTIVE.");
            }
        }

        Page<Book> booksPage = bookRepository.findBySubjectId(
                subjectId,
                (search != null && !search.trim().isEmpty()) ? search : null,
                statusEnum,
                pageable
        );
        return booksPage.map(bookMapper::toBookResponse);
    }


}
