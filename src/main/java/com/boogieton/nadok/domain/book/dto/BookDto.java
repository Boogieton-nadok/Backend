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
        private Long mainId;
        private Long bookId;
        private String title;
        private String author;
        private String isbn;
        private String coverUrl;
        private String bookIntro;
        private String publisher;
        private Integer pageCount;
        private String publishYear;
        private Integer currentPage;
        private ReadingStatus readingStatus;
        private LocalDate startDate;
        private LocalDate endDate;
        private boolean inMyStudy;

        public static BookDetailRes of(Book book, Long mainId, ReadingStatus readingStatus, LocalDate startDate, LocalDate endDate, boolean inMyStudy) {
            return BookDetailRes.builder()
                    .mainId(mainId)
                    .bookId(book.getBookId())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .isbn(book.getIsbn())
                    .coverUrl(book.getCoverUrl())
                    .bookIntro(book.getBookIntro())
                    .publisher(book.getPublisher())
                    .pageCount(book.getPageCount())
                    .publishYear(null)
                    .currentPage(null)
                    .readingStatus(readingStatus)
                    .startDate(startDate)
                    .endDate(endDate)
                    .inMyStudy(inMyStudy)
                    .build();
        }

        public static BookDetailRes from(Book book) {
            return BookDetailRes.builder()
                    .mainId(null)
                    .bookId(book.getBookId())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .isbn(book.getIsbn())
                    .coverUrl(book.getCoverUrl())
                    .bookIntro(book.getBookIntro())
                    .publisher(book.getPublisher())
                    .pageCount(book.getPageCount())
                    .publishYear(null)
                    .currentPage(null)
                    .inMyStudy(false)
                    .build();
        }
    }
}
