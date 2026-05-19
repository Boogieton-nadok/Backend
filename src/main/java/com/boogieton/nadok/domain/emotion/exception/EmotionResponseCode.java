package com.boogieton.nadok.domain.emotion.exception;

import com.boogieton.nadok.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmotionResponseCode implements BaseResponseCode {
    USER_NOT_FOUND("EMOTION_404_1", 404, "사용자를 찾을 수 없습니다."),
    CHARACTER_NOT_FOUND("EMOTION_404_2", 404, "캐릭터를 찾을 수 없습니다."),
    AI_SERVICE_ERROR("EMOTION_500_1", 500, "AI 서비스 호출에 실패했습니다."),
    AI_RESPONSE_PARSE_ERROR("EMOTION_500_2", 500, "AI 응답 파싱에 실패했습니다."),
    AI_REQUEST_CONFUSION_ERROR("EMOTION_503_1", 503, "현재 AI 서버에 접속자가 많아 혼잡합니다. 잠시 후 다시 시도해주세요."),
    INVALID_DIARY("EMOTION_400_1", 400, "일기 형식의 텍스트를 입력해주세요.");

    private final String code;
    private final int httpStatus;
    private final String message;
}