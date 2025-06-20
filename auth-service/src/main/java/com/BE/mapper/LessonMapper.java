package com.BE.mapper;

import com.BE.model.entity.Lesson;
import com.BE.model.request.LessonRequest;
import com.BE.model.response.LessonResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LessonMapper {
    Lesson toLesson(LessonRequest lessonRequest);
    @Mapping(target = "chapter", source = "chapter")
    LessonResponse toLessonResponse(Lesson lesson);

}
