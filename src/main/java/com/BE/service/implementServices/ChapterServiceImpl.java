package com.BE.service.implementServices;

import com.BE.enums.StatusEnum;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.ChapterMapper;
import com.BE.model.entity.Book;
import com.BE.model.entity.Chapter;
import com.BE.model.request.ChapterRequest;
import com.BE.model.response.ChapterResponse;
import com.BE.repository.BookRepository;
import com.BE.repository.ChapterRepository;
import com.BE.service.interfaceServices.IChapterService;
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
public class ChapterServiceImpl implements IChapterService {

    @Autowired
    PageUtil pageUtil;

    @Autowired
    DateNowUtils dateNowUtils;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    ChapterRepository chapterRepository;

    @Autowired
    ChapterMapper chapterMapper;


    @Override
    public ChapterResponse createChapter(ChapterRequest request) {
        // Kiểm tra tên chương đã tồn tại trong cùng một Book chưa
        if (chapterRepository.findByNameAndBookId(request.getName(), request.getBookId()).isPresent()) {
            throw new DataIntegrityViolationException("Chapter with name '" + request.getName() + "' already exists for Book ID: " + request.getBookId());
        }

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + request.getBookId()));

        Chapter chapter = chapterMapper.toChapter(request);
        chapter.setBook(book); // Set Book entity
        chapter.setStatus(StatusEnum.ACTIVE); // Mặc định là ACTIVE khi tạo
        chapter.setCreatedAt(dateNowUtils.dateNow());
        chapter.setUpdatedAt(dateNowUtils.dateNow());

        Chapter savedChapter = chapterRepository.save(chapter);
        return chapterMapper.toChapterResponse(savedChapter);
    }

    @Override
    public Page<ChapterResponse> getAllChapters(int page, int size, String search, String status, String sortBy, String sortDirection) {
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

        Page<Chapter> chaptersPage = chapterRepository.findAllChapters(
                (search != null && !search.trim().isEmpty()) ? search : null,
                statusEnum,
                pageable
        );
        return chaptersPage.map(chapterMapper::toChapterResponse);
    }

    @Override
    public ChapterResponse getChapterById(long id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Chapter not found with ID: " + id));
        return chapterMapper.toChapterResponse(chapter);
    }

    @Override
    public ChapterResponse updateChapter(long id, ChapterRequest request) {
        Chapter existingChapter = chapterRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Chapter not found with ID: " + id));

        // Kiểm tra xem Book ID có thay đổi không
        if (existingChapter.getBook().getId() != request.getBookId()) {
            Book newBook = bookRepository.findById(request.getBookId())
                    .orElseThrow(() -> new NotFoundException("New Book not found with ID: " + request.getBookId()));
            existingChapter.setBook(newBook);
        }

        // Kiểm tra tên chương có trùng với chương khác trong CÙNG book không
        boolean nameChanged = !existingChapter.getName().equalsIgnoreCase(request.getName());
        boolean bookChanged = existingChapter.getBook().getId() != request.getBookId();

        if (nameChanged || bookChanged) {
            Optional<Chapter> duplicateChapter = chapterRepository.findByNameAndBookIdAndIdNot(request.getName(), request.getBookId(), id);
            if (duplicateChapter.isPresent()) {
                throw new DataIntegrityViolationException("Chapter with name '" + request.getName() + "' already exists for Book ID: " + request.getBookId());
            }
        }

        existingChapter.setName(request.getName());
        existingChapter.setUpdatedAt(dateNowUtils.dateNow());

        try {
            Chapter updatedChapter = chapterRepository.save(existingChapter);
            return chapterMapper.toChapterResponse(updatedChapter);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Failed to update chapter: Chapter with name '" + request.getName() + "' already exists for Book ID: " + request.getBookId() + " (Possible race condition).");
        }
    }

    @Override
    public ChapterResponse changeChapterStatus(long id, String newStatus) {
        Chapter existingChapter = chapterRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Chapter not found with ID: " + id));

        StatusEnum statusEnum = null;
        try {
            statusEnum = StatusEnum.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + newStatus + ". Must be ACTIVE or INACTIVE.");
        }

        existingChapter.setStatus(statusEnum);
        existingChapter.setUpdatedAt(dateNowUtils.dateNow());

        Chapter updatedChapter = chapterRepository.save(existingChapter);
        return chapterMapper.toChapterResponse(updatedChapter);
    }

    @Override
    public Page<ChapterResponse> getChaptersByBookId(Long bookId, int page, int size, String search, String status, String sortBy, String sortDirection) {
//        pageUtil.checkOffset(page);
        Pageable pageable = pageUtil.getPageable(page, size, sortBy, sortDirection);

        // Kiểm tra sự tồn tại của Book
        if (!bookRepository.existsById(bookId)) {
            throw new NotFoundException("Book not found with ID: " + bookId);
        }

        StatusEnum statusEnum = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                statusEnum = StatusEnum.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status value: " + status + ". Must be ACTIVE or INACTIVE.");
            }
        }

        Page<Chapter> chaptersPage = chapterRepository.findByBookId(
                bookId,
                (search != null && !search.trim().isEmpty()) ? search : null,
                statusEnum,
                pageable
        );
        return chaptersPage.map(chapterMapper::toChapterResponse);
    }
}
