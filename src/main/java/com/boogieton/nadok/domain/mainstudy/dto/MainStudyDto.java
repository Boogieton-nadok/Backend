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
        private String coverUrl;
        private String bookIntro;
        private String publisher;
        private Integer pageCount;
        @NotNull
        private ReadingStatus readingStatus;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Getter
    public static class UpdateReq {
        @NotNull
        private ReadingStatus readingStatus;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Getter
    @Builder
    public static class StudyListRes {
        private Long mainId;
        private Long bookId;
        private String isbn;
        private String title;
        private String author;
        private String coverUrl;
        private ReadingStatus readingStatus;
        private LocalDate startDate;
        private LocalDate endDate;

        public static StudyListRes from(MainStudy mainStudy) {
            return StudyListRes.builder()
                    .mainId(mainStudy.getMainId())
                    .bookId(mainStudy.getBook().getBookId())
                    .isbn(mainStudy.getBook().getIsbn())
                    .title(mainStudy.getBook().getTitle())
                    .author(mainStudy.getBook().getAuthor())
                    .coverUrl(mainStudy.getBook().getCoverUrl())
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
        private String coverUrl;
        private String bookIntro;
        private String publisher;
        private Integer pageCount;
        private ReadingStatus readingStatus;
        private LocalDate startDate;
        private LocalDate endDate;
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
                    .pageCount(mainStudy.getBook().getPageCount())
                    .readingStatus(mainStudy.getReadingStatus())
                    .startDate(mainStudy.getStartDate())
                    .endDate(mainStudy.getEndDate())
                    .isInMyStudy(true)
                    .build();
        }
    }
}
