package com.mpie.service1.mapper;

import com.mpie.service1.model.Book;
import com.mpie.service1.model.BookDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {

    Book toEntity(BookDto bookDto);
    BookDto toDto(Book book);
    List<BookDto> toDtos(List<Book> book);
}
