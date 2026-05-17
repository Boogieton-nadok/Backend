package com.boogieton.nadok.domain.book.exception;

import com.boogieton.nadok.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookResponseCode implements BaseResponseCode {
    BOOK_NOT_FOUND("BOOK_404_1", 404, "도서를 찾을 수 없습니다."),
    BOOK_SEARCH_FAILED("BOOK_500_1", 500, "외부 도서 검색에 실패했습니다."),
    BOOK_SEARCH_EMPTY("BOOK_404_2", 404, "검색 결과가 없습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
