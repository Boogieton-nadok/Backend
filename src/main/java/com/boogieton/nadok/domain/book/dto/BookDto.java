package com.boogieton.nadok.domain.book.dto;

import com.boogieton.nadok.domain.book.entity.Book;
import com.boogieton.nadok.domain.book.entity.ReadingStatus;
import com.boogieton.nadok.domain.mainstudy.entity.MainStudy;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class BookDto {

    // 💡 4번(camelCase) 및 5번(값이 없을 때 null) 규칙을 적용하여 BookSearchRes 수정
    @Getter
    @Builder
    public static class BookSearchRes {
        private String title;
        private String author;
        private String isbn;
        private String coverUrl;    // camelCase 통일
        private String publisher;
        private String publishYear; // 추가: 검색 리스트에서도 노출하면 프론트가 빌드하기 좋습니다.
        private Integer pageCount;  // camelCase 통일
        private String bookIntro;   // camelCase 통일

        // 알라딘 API 응답 데이터를 객체로 변환할 때 null 처리를 안전하게 하기 위한 팩토리 메서드 추가
        public static BookSearchRes of(String title, String author, String isbn, String coverUrl,
                                       String publisher, String rawPublishYear, Integer rawPageCount, String bookIntro) {

            // 값이 없거나 공백 문자열이면 null 처리
            String processedPublishYear = (rawPublishYear == null || rawPublishYear.isBlank()) ? null : rawPublishYear;
            String processedCoverUrl = (coverUrl == null || coverUrl.isBlank()) ? null : coverUrl;
            String processedBookIntro = (bookIntro == null || bookIntro.isBlank()) ? null : bookIntro;
            String processedPublisher = (publisher == null || publisher.isBlank()) ? null : publisher;

            // 페이지 수가 없거나 0이면 null 처리
            Integer processedPageCount = (rawPageCount == null || rawPageCount == 0) ? null : rawPageCount;

            return BookSearchRes.builder()
                    .title(title)
                    .author(author)
                    .isbn(isbn)
                    .coverUrl(processedCoverUrl)
                    .publisher(processedPublisher)
                    .publishYear(processedPublishYear)
                    .pageCount(processedPageCount)
                    .bookIntro(processedBookIntro)
                    .build();
        }
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
            String publishYear = (book.getPublishYear() == null || book.getPublishYear().isBlank()) ? null : book.getPublishYear();
            Integer pageCount = (book.getPageCount() == null || book.getPageCount() == 0) ? null : book.getPageCount();
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
                    .publishYear(publishYear)
                    .pageCount(pageCount)
                    .currentPage(currentPage)
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
                    .publishYear(publishYear)
                    .pageCount(pageCount)
                    .currentPage(null)
                    .readingStatus(null)
                    .startDate(null)
                    .endDate(null)
                    .isInMyStudy(false)
                    .build();
        }
    }
}