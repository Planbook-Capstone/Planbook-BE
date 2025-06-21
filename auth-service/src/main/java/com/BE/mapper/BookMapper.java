package com.BE.mapper;

import com.BE.model.entity.Book;
import com.BE.model.request.BookRequest;
import com.BE.model.response.BookResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {

    Book toBook(BookRequest bookRequest);
    @Mapping(target = "subject", source = "subject")
    BookResponse toBookResponse(Book book);
}
