package com.boogieton.nadok.domain.book.dto;

import com.boogieton.nadok.domain.book.entity.Book;
import com.boogieton.nadok.domain.book.entity.ReadingStatus;
import com.boogieton.nadok.domain.mainstudy.entity.MainStudy;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

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
        private String publishYear;   // 💡 추가
        private Integer pageCount;
        private Integer currentPage;  // 💡 추가
        private ReadingStatus readingStatus;
        private LocalDate startDate;
        private LocalDate endDate;
        private boolean isInMyStudy;

        // 1. 내 서재에 등록된 도서 응답 (ms가 존재할 때)
        public static BookDetailRes of(MainStudy mainStudy, Book book, ReadingStatus readingStatus, LocalDate startDate, LocalDate endDate, boolean isInMyStudy) {
            return BookDetailRes.builder()
                    .mainId(mainStudy.getMainId())
                    .bookId(book.getBookId())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .isbn(book.getIsbn())
                    .coverUrl(book.getCoverUrl())
                    .bookIntro(book.getBookIntro())
                    .publisher(book.getPublisher())
                    .publishYear(book.getPublishYear())
                    .pageCount(book.getPageCount())
                    .currentPage(book.getPageCount())
                    .readingStatus(readingStatus)
                    .startDate(startDate)
                    .endDate(endDate)
                    .isInMyStudy(isInMyStudy)
                    .build();
        }

        // 2. 내 서재에 등록되지 않은 도서 응답 (💡 파라미터에서 MainStudy 제거하여 버그 수정)
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
                    .publishYear(book.getPublishYear())
                    .pageCount(book.getPageCount())
                    .currentPage(book.getPageCount())
                    .readingStatus(null)
                    .startDate(null)
                    .endDate(null)
                    .isInMyStudy(false)
                    .build();
        }
    }
}