package com.mpie.service1.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@AllArgsConstructor
public class Book {
    @Id
    private String isbn;
    private String author;
    private String title;
    private String category;
    @Setter
    private String borrower;
    @Setter
    private BookAvailabilityStatus bookStatus;
}