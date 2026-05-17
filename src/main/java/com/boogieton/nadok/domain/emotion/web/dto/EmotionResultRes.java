package com.boogieton.nadok.domain.emotion.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EmotionResultRes {
    private Long inputId;
    private Long resultId;
    private String methodReason;
    private CharacterInfo character;
    private LocalDateTime createdAt;

    @Getter
    @Builder
    public static class CharacterInfo {
        private Long characterId;
        private String characterName;
        private String characterImgUrl;
        private String bookQuote;
    }
}
