package com.BE.service.implementServices;

import com.BE.mapper.LessonMapper;
import com.BE.repository.ChapterRepository;
import com.BE.repository.LessonRepository;
import com.BE.service.interfaceServices.ILessonService;
import com.BE.utils.DateNowUtils;
import com.BE.utils.PageUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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



}
