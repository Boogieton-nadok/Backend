package com.boogieton.nadok.domain.emotion.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmotionInputReq {
    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "일기 내용을 입력해주세요.")
    private String inputText;

    @NotNull(message = "감정 태그를 입력해주세요.")
    private String emotionTag;

    @NotNull(message = "위로 방식을 입력해주세요.")
    private String comfortMethod;
}
