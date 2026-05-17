package com.boogieton.nadok.domain.book.controller;

import com.boogieton.nadok.domain.book.dto.BookDto.*;
import com.boogieton.nadok.domain.book.service.BookService;
import com.boogieton.nadok.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/api/book/search")
    public SuccessResponse<List<BookSearchRes>> searchBooks(@RequestParam String query) {
        return SuccessResponse.from(bookService.searchBooks(query));
    }

    @GetMapping("/api/books/{isbn}")
    public SuccessResponse<BookDetailRes> getBookDetail(
            @PathVariable String isbn,
            @RequestParam Long userId) {
        return SuccessResponse.from(bookService.getBookDetail(isbn, userId));
    }
}
