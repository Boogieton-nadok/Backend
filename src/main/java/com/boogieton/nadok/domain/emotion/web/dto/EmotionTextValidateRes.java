package com.boogieton.nadok.domain.emotion.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmotionTextValidateRes {

    private boolean isValid;
}
