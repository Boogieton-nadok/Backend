package com.boogieton.nadok.domain.book.dto;

import com.boogieton.nadok.domain.book.entity.Book;
import com.boogieton.nadok.domain.book.entity.ReadingStatus;
import com.boogieton.nadok.domain.mainstudy.entity.MainStudy;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class BookDto {

    // 알라딘 검색 응답 DTO도 동일한 규칙 적용
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
        private String publishYear;
        private Integer pageCount;
        private Integer currentPage;
        private ReadingStatus readingStatus;
        private LocalDate startDate;
        private LocalDate endDate;
        private boolean isInMyStudy;

        // 1. 내 서재에 등록된 도서 상세 응답
        public static BookDetailRes of(MainStudy mainStudy, Book book, ReadingStatus readingStatus, LocalDate startDate, LocalDate endDate, boolean isInMyStudy) {
            // 💡 값이 비어있으면 null로 치환하는 헬퍼 로직 적용
            String publishYear = (book.getPublishYear() == null || book.getPublishYear().isBlank()) ? null : book.getPublishYear();
            Integer pageCount = (book.getPageCount() == null || book.getPageCount() == 0) ? null : book.getPageCount();

            // "페이지 정보 = 총 페이지 수" 규칙에 따르되, 총 페이지가 null이면 현재 페이지도 null
            Integer currentPage = pageCount;

            return BookDetailRes.builder()
                    .mainId(mainStudy.getMainId())
                    .bookId(book.getBookId())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .isbn(book.getIsbn())
                    .coverUrl(book.getCoverUrl())
                    .bookIntro(book.getBookIntro())
                    .publisher(book.getPublisher())
                    .publishYear(publishYear)   // 💡 null 처리된 값 대입
                    .pageCount(pageCount)       // 💡 null 처리된 값 대입
                    .currentPage(currentPage)   // 💡 null 처리된 값 대입
                    .readingStatus(readingStatus)
                    .startDate(startDate)
                    .endDate(endDate)
                    .isInMyStudy(isInMyStudy)
                    .build();
        }

        // 2. 내 서재에 등록되지 않은 도서 상세 응답
        public static BookDetailRes from(Book book) {
            String publishYear = (book.getPublishYear() == null || book.getPublishYear().isBlank()) ? null : book.getPublishYear();
            Integer pageCount = (book.getPageCount() == null || book.getPageCount() == 0) ? null : book.getPageCount();

            return BookDetailRes.builder()
                    .mainId(null)
                    .bookId(book.getBookId())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .isbn(book.getIsbn())
                    .coverUrl(book.getCoverUrl())
                    .bookIntro(book.getBookIntro())
                    .publisher(book.getPublisher())
                    .publishYear(publishYear)   // 💡 null 처리된 값 대입
                    .pageCount(pageCount)       // 💡 null 처리된 값 대입
                    .currentPage(null)          // 💡 서재에 없는 책이므로 0 대신 명확히 null 반환
                    .readingStatus(null)
                    .startDate(null)
                    .endDate(null)
                    .isInMyStudy(false)
                    .build();
        }
    }
}