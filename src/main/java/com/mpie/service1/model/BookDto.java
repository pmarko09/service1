package com.mpie.service1.model;

public record BookDto(String isbn, String author, String title, String category, String borrower,
                      BookAvailabilityStatus bookStatus) {
}