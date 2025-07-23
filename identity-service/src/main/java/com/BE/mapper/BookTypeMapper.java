package com.BE.mapper;

import com.BE.model.entity.BookType;
import com.BE.model.request.BookTypeRequest;
import com.BE.model.response.BookTypeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookTypeMapper {

    BookType toBookType(BookTypeRequest request);

    // Map BookType entity to BookTypeResponse DTO
    BookTypeResponse toBookTypeResponse(BookType bookType);
}