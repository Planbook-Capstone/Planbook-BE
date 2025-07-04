package com.BE.mapper;

import com.BE.model.entity.Chapter;
import com.BE.model.request.ChapterRequest;
import com.BE.model.response.ChapterResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChapterMapper {
    Chapter toChapter(ChapterRequest chapterRequest);
    @Mapping(target = "book", source = "book")
    ChapterResponse toChapterResponse(Chapter chapter);


}
