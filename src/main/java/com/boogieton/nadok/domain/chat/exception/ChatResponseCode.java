package com.boogieton.nadok.domain.chat.exception;


import com.boogieton.nadok.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatResponseCode implements BaseResponseCode {
    CHAT_ROOM_NOT_FOUND("CHAT_404_1", 404, "존재하지 않는 채팅방입니다."),
    AI_RESPONSE_ERROR("CHAT_500_1", 500, "가독이(AI)와의 연결이 원활하지 않습니다."),
    SEARCH_RESULT_NOT_FOUND("CHAT_404_2", 404, "검색 결과가 존재하지 않습니다.");


    private final String code;
    private final int httpStatus;
    private final String message;
}