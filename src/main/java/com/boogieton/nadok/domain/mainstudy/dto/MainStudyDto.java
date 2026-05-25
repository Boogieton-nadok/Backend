package com.boogieton.nadok.domain.mainstudy.dto;

import com.boogieton.nadok.domain.book.entity.ReadingStatus;
import com.boogieton.nadok.domain.mainstudy.entity.MainStudy;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class MainStudyDto {

    @Getter
    public static class CreateReq {
        @NotNull
        private String isbn;
        private String title;
        private String author;
        private String coverUrl;   // 💡 camelCase 통일
        private String bookIntro;  // 💡 camelCase 통일
        private String publisher;
        private String publishYear; // 💡 추가
        private Integer pageCount;  // 💡 camelCase 통일
        @NotNull
        private ReadingStatus readingStatus;
        private LocalDate startDate; // 💡 camelCase 통일
        private LocalDate endDate;   // 💡 camelCase 통일
    }

    @Getter
    public static class UpdateReq {
        @NotNull
        private ReadingStatus readingStatus;
        private LocalDate startDate; // 💡 camelCase 통일
        private LocalDate endDate;   // 💡 camelCase 통일
    }

    @Getter
    @Builder
    public static class StudyListRes {
        private Long mainId;
        private Long bookId;
        private String title;
        private String author;
        private String coverUrl;   // 💡 camelCase 통일
        private String isbn;       // 💡 ISBN -> isbn 명명 규칙 통일
        private ReadingStatus readingStatus;
        private LocalDate startDate; // 💡 camelCase 통일
        private LocalDate endDate;   // 💡 camelCase 통일

        public static StudyListRes from(MainStudy mainStudy) {
            return StudyListRes.builder()
                    .mainId(mainStudy.getMainId())
                    .bookId(mainStudy.getBook().getBookId())
                    .title(mainStudy.getBook().getTitle())
                    .author(mainStudy.getBook().getAuthor())
                    .coverUrl(mainStudy.getBook().getCoverUrl())
                    .isbn(mainStudy.getBook().getIsbn())
                    .readingStatus(mainStudy.getReadingStatus())
                    .startDate(mainStudy.getStartDate())
                    .endDate(mainStudy.getEndDate())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class StudyDetailRes {
        private Long mainId;
        private Long bookId;
        private String title;
        private String author;
        private String isbn;
        private String coverUrl;   // 💡 camelCase 통일
        private String bookIntro;  // 💡 camelCase 통일
        private String publisher;
        private String publishYear; // 💡 도서 상세와 규격 통일
        private Integer pageCount;  // 💡 camelCase 통일
        private Integer currentPage; // 💡 도서 상세와 규격 통일
        private ReadingStatus readingStatus;
        private LocalDate startDate; // 💡 camelCase 통일
        private LocalDate endDate;   // 💡 camelCase 통일
        private boolean isInMyStudy;

        public static StudyDetailRes from(MainStudy mainStudy) {
            return StudyDetailRes.builder()
                    .mainId(mainStudy.getMainId())
                    .bookId(mainStudy.getBook().getBookId())
                    .title(mainStudy.getBook().getTitle())
                    .author(mainStudy.getBook().getAuthor())
                    .isbn(mainStudy.getBook().getIsbn())
                    .coverUrl(mainStudy.getBook().getCoverUrl())
                    .bookIntro(mainStudy.getBook().getBookIntro())
                    .publisher(mainStudy.getBook().getPublisher())
                    .publishYear(mainStudy.getBook().getPublishYear())
                    .pageCount(mainStudy.getBook().getPageCount())
                    .currentPage(mainStudy.getBook().getPageCount()) // 💡 "페이지 정보 = 총 페이지 수" 반영
                    .readingStatus(mainStudy.getReadingStatus())
                    .startDate(mainStudy.getStartDate())
                    .endDate(mainStudy.getEndDate())
                    .isInMyStudy(true)
                    .build();
        }
    }
}