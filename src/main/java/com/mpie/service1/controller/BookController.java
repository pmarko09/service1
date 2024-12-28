package com.mpie.service1.controller;

import com.mpie.service1.model.BookDto;
import com.mpie.service1.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    @GetMapping
    public List<BookDto> getBooks() {
        log.info("Retrieved get all books request.");
        return bookService.getBooks();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto createBook(@RequestBody BookDto bookDto) {
        log.info("Retrieved create book request with data: {}", bookDto);
        return bookService.createBook(bookDto);
    }

    @PutMapping("/rent")
    public BookDto rentBook(@RequestParam String clientName, @RequestParam String isbn) {
        log.info("Retrieved rent request for book: {} and client: {}", isbn, clientName);
        return bookService.rentBook(clientName, isbn);
    }

    @PutMapping("/return")
    public BookDto returnBook(@RequestParam String isbn) {
        log.info("Retrieved return request for book: {}", isbn);
        return bookService.returnBook(isbn);
    }
}