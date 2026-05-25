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
        private String coverUrl;   // 💡 camelCase 통일
        private String publisher;
        private Integer pageCount;  // 💡 camelCase 통일
        private String bookIntro;  // 💡 camelCase 통일
    }

    @Getter
    @Builder
    public static class BookDetailRes {
        private Long mainId;
        private Long bookId;
        private String title;
        private String author;
        private String isbn;
        private String coverUrl;   // 💡 camelCase 통일
        private String bookIntro;  // 💡 camelCase 통일
        private String publisher;
        private String publishYear;
        private Integer pageCount;  // 💡 camelCase 통일
        private Integer currentPage;
        private ReadingStatus readingStatus;
        private LocalDate startDate; // 💡 camelCase 통일
        private LocalDate endDate;   // 💡 camelCase 통일
        private boolean isInMyStudy;

        // 1. 내 서재에 등록된 도서 상세 응답
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
                    .currentPage(book.getPageCount()) // 💡 "페이지 정보 = 총 페이지 수" 반영
                    .readingStatus(readingStatus)
                    .startDate(startDate)
                    .endDate(endDate)
                    .isInMyStudy(isInMyStudy)
                    .build();
        }

        // 2. 내 서재에 등록되지 않은 도서 상세 응답 (MainStudy 파라미터 버그 수정)
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