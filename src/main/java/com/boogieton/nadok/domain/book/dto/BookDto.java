package com.boogieton.nadok.domain.book.dto;

import com.boogieton.nadok.domain.book.entity.Book;
import com.boogieton.nadok.domain.book.entity.ReadingStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class BookDto {

    @Getter
    @Builder
    public static class BookSearchRes {
        private String title;
        private String author;
        private String isbn;
        private String coverUrl;
        private String publisher;
        private Integer pageCount;
        private String bookIntro;
    }

    @Getter
    @Builder
    public static class BookDetailRes {
        private Long bookId;
        private String title;
        private String author;
        private String isbn;
        private String coverUrl;
        private String bookIntro;
        private String publisher;
        private Integer pageCount;
        private ReadingStatus readingStatus;
        private LocalDate startDate;
        private LocalDate endDate;
        private boolean isInMyStudy;

        public static BookDetailRes of(Book book, ReadingStatus readingStatus, LocalDate startDate, LocalDate endDate, boolean isInMyStudy) {
            return BookDetailRes.builder()
                    .bookId(book.getBookId())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .isbn(book.getIsbn())
                    .coverUrl(book.getCoverUrl())
                    .bookIntro(book.getBookIntro())
                    .publisher(book.getPublisher())
                    .pageCount(book.getPageCount())
                    .readingStatus(readingStatus)
                    .startDate(startDate)
                    .endDate(endDate)
                    .isInMyStudy(isInMyStudy)
                    .build();
        }

        public static BookDetailRes from(Book book) {
            return BookDetailRes.builder()
                    .bookId(book.getBookId())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .isbn(book.getIsbn())
                    .coverUrl(book.getCoverUrl())
                    .bookIntro(book.getBookIntro())
                    .publisher(book.getPublisher())
                    .pageCount(book.getPageCount())
                    .isInMyStudy(false)
                    .build();
        }
    }
}
