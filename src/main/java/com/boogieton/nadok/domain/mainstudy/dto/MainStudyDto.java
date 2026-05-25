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

        public static StudyDetailRes from(MainStudy mainStudy) {
            // 💡 값이 비어있거나 0인 경우 null 처리
            String publishYear = (mainStudy.getBook().getPublishYear() == null || mainStudy.getBook().getPublishYear().isBlank()) ? null : mainStudy.getBook().getPublishYear();
            Integer pageCount = (mainStudy.getBook().getPageCount() == null || mainStudy.getBook().getPageCount() == 0) ? null : mainStudy.getBook().getPageCount();

            // 내 서재에 등록된 상태이므로 총 페이지 수를 할당하되, 없으면 null
            Integer currentPage = pageCount;

            return StudyDetailRes.builder()
                    .mainId(mainStudy.getMainId())
                    .bookId(mainStudy.getBook().getBookId())
                    .title(mainStudy.getBook().getTitle())
                    .author(mainStudy.getBook().getAuthor())
                    .isbn(mainStudy.getBook().getIsbn())
                    .coverUrl(mainStudy.getBook().getCoverUrl())
                    .bookIntro(mainStudy.getBook().getBookIntro())
                    .publisher(mainStudy.getBook().getPublisher())
                    .publishYear(publishYear)   // 💡 null 처리된 값 대입
                    .pageCount(pageCount)       // 💡 null 처리된 값 대입
                    .currentPage(currentPage)   // 💡 null 처리된 값 대입
                    .readingStatus(mainStudy.getReadingStatus())
                    .startDate(mainStudy.getStartDate())
                    .endDate(mainStudy.getEndDate())
                    .isInMyStudy(true)
                    .build();
        }
    }
}