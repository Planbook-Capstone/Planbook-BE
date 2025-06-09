package com.BE.service.implementServices;

import com.BE.mapper.ChapterMapper;
import com.BE.repository.BookRepository;
import com.BE.repository.ChapterRepository;
import com.BE.service.interfaceServices.IChapterService;
import com.BE.utils.DateNowUtils;
import com.BE.utils.PageUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
