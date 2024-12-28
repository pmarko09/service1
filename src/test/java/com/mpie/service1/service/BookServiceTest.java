package com.mpie.service1.service;

import com.mpie.service1.mapper.BookMapper;
import com.mpie.service1.model.Book;
import com.mpie.service1.model.BookAvailabilityStatus;
import com.mpie.service1.model.BookDto;
import com.mpie.service1.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private KafkaSender kafkaSender;

    @InjectMocks
    private BookService bookService;

    @Test
    public void testGetBooks() {
        // Given
        List<Book> books = Arrays.asList(
                new Book("123", "Author 1", "Book 1", "Fiction", null, BookAvailabilityStatus.AVAILABLE),
                new Book("456", "Author 2", "Book 2", "Non-fiction", null, BookAvailabilityStatus.AVAILABLE)
        );
        when(bookRepository.findAll()).thenReturn(books);

        List<BookDto> expectedBookDtos = Arrays.asList(
                new BookDto("123", "Author 1", "Book 1", "Fiction", null, BookAvailabilityStatus.AVAILABLE),
                new BookDto("456", "Author 2", "Book 2", "Non-fiction", null, BookAvailabilityStatus.AVAILABLE)
        );
        when(bookMapper.toDtos(books)).thenReturn(expectedBookDtos);

        // When
        List<BookDto> actualBookDtos = bookService.getBooks();

        // Then
        assertEquals(expectedBookDtos.size(), actualBookDtos.size());
        for (int i = 0; i < expectedBookDtos.size(); i++) {
            assertEquals(expectedBookDtos.get(i), actualBookDtos.get(i));
        }
    }

    @Test
    public void testCreateBook() {
        // Given
        BookDto bookDto = new BookDto("123", "Author 1", "Book 1", "Fiction", null, BookAvailabilityStatus.AVAILABLE);
        Book bookEntity = new Book("123", "Author 1", "Book 1", "Fiction", null, BookAvailabilityStatus.AVAILABLE);
        when(bookMapper.toEntity(bookDto)).thenReturn(bookEntity);

        Book savedBookEntity = new Book("123", "Author 1", "Book 1", "Fiction", null, BookAvailabilityStatus.AVAILABLE);
        when(bookRepository.save(bookEntity)).thenReturn(savedBookEntity);

        BookDto expectedBookDto = new BookDto("123", "Author 1", "Book 1", "Fiction", null, BookAvailabilityStatus.AVAILABLE);
        when(bookMapper.toDto(savedBookEntity)).thenReturn(expectedBookDto);

        // When
        BookDto createdBookDto = bookService.createBook(bookDto);

        // Then
        assertEquals(expectedBookDto, createdBookDto);
        verify(bookRepository).save(bookEntity);
    }

    @Test
    public void testRentBook() {
        // Given
        String clientName = "John Doe";
        String isbn = "123";

        Book bookEntity = new Book("123", "Author 1", "Book 1", "Fiction", null, BookAvailabilityStatus.AVAILABLE);
        when(bookRepository.findById(isbn)).thenReturn(Optional.of(bookEntity));

        BookDto expectedBookDto = new BookDto("123", "Author 1", "Book 1", "Fiction", clientName, BookAvailabilityStatus.AVAILABLE);
        when(bookMapper.toDto(bookEntity)).thenReturn(expectedBookDto);

        // When
        BookDto rentedBookDto = bookService.rentBook(clientName, isbn);

        // Then
        assertEquals(clientName, bookEntity.getBorrower());
        verify(bookRepository).save(bookEntity);
        verify(kafkaSender).sendBookRented(bookEntity);
        assertEquals(expectedBookDto, rentedBookDto);
    }

    @Test
    public void testRentBook_BookNotFound() {
        // Given
        String clientName = "John Doe";
        String isbn = "123";

        when(bookRepository.findById(isbn)).thenReturn(Optional.empty());

        // When / Then
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> bookService.rentBook(clientName, isbn));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Book with given isbn does not exist", ex.getReason());
    }

    @Test
    public void testRentBook_BookAlreadyRented() {
        // Given
        String clientName = "John Doe";
        String isbn = "123";

        Book alreadyRentedBook = new Book("123", "Author 1", "Book 1", "Fiction", "Alice", BookAvailabilityStatus.AVAILABLE);
        when(bookRepository.findById(isbn)).thenReturn(Optional.of(alreadyRentedBook));

        // When / Then
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> bookService.rentBook(clientName, isbn));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Book with the given isbn number is currently unavailable.", ex.getReason());
    }
}
