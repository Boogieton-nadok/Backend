package com.boogieton.nadok.domain.emotion.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmotionTextValidateReq {

    @NotBlank(message = "일기 내용을 입력해주세요.")
    private String inputText;
}
