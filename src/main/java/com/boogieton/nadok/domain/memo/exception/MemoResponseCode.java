package com.boogieton.nadok.domain.memo.exception;

import com.boogieton.nadok.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemoResponseCode implements BaseResponseCode {
    MEMO_NOT_FOUND("MEMO_404_1", 404, "메모를 찾을 수 없습니다."),
    MEMO_SAVE_SUCCESS("MEMO_201_1", 201, "메모가 등록되었습니다."),
    MEMO_UPDATE_SUCCESS("MEMO_200_1", 200, "메모가 수정되었습니다."),
    MEMO_DELETE_SUCCESS("MEMO_200_2", 200, "메모가 삭제되었습니다."),
    MEMO_LIST_GET_SUCCESS("MEMO_200_3", 200, "메모 리스트를 성공적으로 불러왔습니다."),
    MEMO_GET_SUCCESS("MEMO_200_4", 200, "메모를 성공적으로 불러왔습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
