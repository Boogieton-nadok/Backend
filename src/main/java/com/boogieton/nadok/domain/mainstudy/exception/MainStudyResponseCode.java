package com.boogieton.nadok.domain.mainstudy.exception;

import com.boogieton.nadok.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MainStudyResponseCode implements BaseResponseCode {
    MAIN_STUDY_NOT_FOUND("STUDY_404_1", 404, "서재에 등록되지 않은 도서입니다."),
    MAIN_STUDY_ALREADY_EXISTS("STUDY_409_1", 409, "이미 서재에 등록된 도서입니다."),
    MAIN_STUDY_ACCESS_DENIED("STUDY_403_1", 403, "해당 서재에 접근 권한이 없습니다."),
    MAIN_STUDY_SAVE_SUCCESS("STUDY_201_1", 201, "서재에 도서가 등록되었습니다."),
    MAIN_STUDY_UPDATE_SUCCESS("STUDY_200_1", 200, "독서 정보가 수정되었습니다."),
    MAIN_STUDY_DELETE_SUCCESS("STUDY_200_2", 200, "서재에서 도서가 삭제되었습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
