package com.BE.service.implementServices;

import com.BE.enums.StatusEnum;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.LessonMapper;
import com.BE.model.entity.Chapter;
import com.BE.model.entity.Lesson;
import com.BE.model.request.LessonRequest;
import com.BE.model.response.LessonResponse;
import com.BE.repository.ChapterRepository;
import com.BE.repository.LessonRepository;
import com.BE.service.interfaceServices.ILessonService;
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
public class LessonServiceImpl implements ILessonService {

    @Autowired
    PageUtil pageUtil;

    @Autowired
    DateNowUtils dateNowUtils;

    @Autowired
    LessonRepository lessonRepository;

    @Autowired
    ChapterRepository chapterRepository;

    @Autowired
    LessonMapper lessonMapper;


    @Override
    public LessonResponse createLesson(LessonRequest request) {
        // Kiểm tra tên bài học đã tồn tại trong cùng một Chapter chưa
        if (lessonRepository.findByNameAndChapterId(request.getName(), request.getChapterId()).isPresent()) {
            throw new DataIntegrityViolationException("Lesson with name '" + request.getName() + "' already exists for Chapter ID: " + request.getChapterId());
        }

        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new NotFoundException("Chapter not found with ID: " + request.getChapterId()));

        Lesson lesson = lessonMapper.toLesson(request);
        lesson.setChapter(chapter); // Set Chapter entity
        lesson.setStatus(StatusEnum.ACTIVE); // Mặc định là ACTIVE khi tạo
        lesson.setCreatedAt(dateNowUtils.dateNow());
        lesson.setUpdatedAt(dateNowUtils.dateNow());

        Lesson savedLesson = lessonRepository.save(lesson);
        return lessonMapper.toLessonResponse(savedLesson);
    }

    @Override
    public Page<LessonResponse> getAllLessons(int page, int size, String search, String status, String sortBy, String sortDirection) {
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

        Page<Lesson> lessonsPage = lessonRepository.findAllLessons(
                (search != null && !search.trim().isEmpty()) ? search : null,
                statusEnum,
                pageable
        );
        return lessonsPage.map(lessonMapper::toLessonResponse);
    }

    @Override
    public LessonResponse getLessonById(long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Lesson not found with ID: " + id));
        return lessonMapper.toLessonResponse(lesson);
    }

    @Override
    public LessonResponse updateLesson(long id, LessonRequest request) {
        Lesson existingLesson = lessonRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Lesson not found with ID: " + id));

        // Kiểm tra xem Chapter ID có thay đổi không
        if (existingLesson.getChapter().getId() != request.getChapterId()) {
            Chapter newChapter = chapterRepository.findById(request.getChapterId())
                    .orElseThrow(() -> new NotFoundException("New Chapter not found with ID: " + request.getChapterId()));
            existingLesson.setChapter(newChapter);
        }

        // Kiểm tra tên bài học có trùng với bài học khác trong CÙNG chapter không
        boolean nameChanged = !existingLesson.getName().equalsIgnoreCase(request.getName());
        boolean chapterChanged = existingLesson.getChapter().getId() != request.getChapterId();

        if (nameChanged || chapterChanged) {
            Optional<Lesson> duplicateLesson = lessonRepository.findByNameAndChapterIdAndIdNot(request.getName(), request.getChapterId(), id);
            if (duplicateLesson.isPresent()) {
                throw new DataIntegrityViolationException("Lesson with name '" + request.getName() + "' already exists for Chapter ID: " + request.getChapterId());
            }
        }

        existingLesson.setName(request.getName());
        existingLesson.setUpdatedAt(dateNowUtils.dateNow());

        try {
            Lesson updatedLesson = lessonRepository.save(existingLesson);
            return lessonMapper.toLessonResponse(updatedLesson);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Failed to update lesson: Lesson with name '" + request.getName() + "' already exists for Chapter ID: " + request.getChapterId() + " (Possible race condition).");
        }
    }

    @Override
    public LessonResponse changeLessonStatus(long id, String newStatus) {
        Lesson existingLesson = lessonRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Lesson not found with ID: " + id));

        StatusEnum statusEnum = null;
        try {
            statusEnum = StatusEnum.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + newStatus + ". Must be ACTIVE or INACTIVE.");
        }

        existingLesson.setStatus(statusEnum);
        existingLesson.setUpdatedAt(dateNowUtils.dateNow());

        Lesson updatedLesson = lessonRepository.save(existingLesson);
        return lessonMapper.toLessonResponse(updatedLesson);
    }

    @Override
    public Page<LessonResponse> getLessonsByChapterId(Long chapterId, int page, int size, String search, String status, String sortBy, String sortDirection) {
//        pageUtil.checkOffset(page);
        Pageable pageable = pageUtil.getPageable(page, size, sortBy, sortDirection);

        // Kiểm tra sự tồn tại của Chapter
        if (!chapterRepository.existsById(chapterId)) {
            throw new NotFoundException("Chapter not found with ID: " + chapterId);
        }

        StatusEnum statusEnum = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                statusEnum = StatusEnum.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status value: " + status + ". Must be ACTIVE or INACTIVE.");
            }
        }

        Page<Lesson> lessonsPage = lessonRepository.findByChapterId(
                chapterId,
                (search != null && !search.trim().isEmpty()) ? search : null,
                statusEnum,
                pageable
        );
        return lessonsPage.map(lessonMapper::toLessonResponse);
    }



}
