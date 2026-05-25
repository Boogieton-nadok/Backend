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
        private String title;
        private String author;
        private String coverUrl;
        private String ISBN;
        private ReadingStatus readingStatus;
        private LocalDate startDate;
        private LocalDate endDate;

        public static StudyListRes from(MainStudy mainStudy) {
            return StudyListRes.builder()
                    .mainId(mainStudy.getMainId())
                    .bookId(mainStudy.getBook().getBookId())
                    .title(mainStudy.getBook().getTitle())
                    .author(mainStudy.getBook().getAuthor())
                    .coverUrl(mainStudy.getBook().getCoverUrl())
                    .ISBN(mainStudy.getBook().getIsbn())
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
        private String publishYear;   // 💡 도서 상세와 통일을 위해 추가
        private Integer pageCount;
        private Integer currentPage;  // 💡 도서 상세와 통일을 위해 추가 ("페이지 정보 = 총 페이지 수")
        private ReadingStatus readingStatus;
        private LocalDate startDate;
        private LocalDate endDate;
        private boolean isInMyStudy; // 💡 Jackson의 JSON 직렬화 명명 규칙 스펙 통일을 위해 primitive 대신 변경 권장되나 프론트 필드명 매핑 유지

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
                    .publishYear(mainStudy.getBook().getPublishYear()) // 💡 추가된 필드 매핑
                    .pageCount(mainStudy.getBook().getPageCount())
                    .currentPage(mainStudy.getBook().getPageCount())  // 💡 "페이지 정보 = 총 페이지 수" 반영
                    .readingStatus(mainStudy.getReadingStatus())
                    .startDate(mainStudy.getStartDate())
                    .endDate(mainStudy.getEndDate())
                    .isInMyStudy(true)
                    .build();
        }
    }
}