package com.boogieton.nadok.domain.memo.dto;

import com.boogieton.nadok.domain.memo.entity.Memo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class MemoDto {

    @Getter
    public static class CreateReq {
        @NotNull
        private Long mainId;
        @NotBlank
        private String content;
    }

    @Getter
    public static class UpdateReq {
        @NotBlank
        private String content;
    }

    @Getter
    @Builder
    public static class MemoRes {
        private Long memoId;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static MemoRes from(Memo memo) {
            return MemoRes.builder()
                    .memoId(memo.getMemoId())
                    .content(memo.getContent())
                    .createdAt(memo.getCreatedAt())
                    .updatedAt(memo.getUpdatedAt())
                    .build();
        }
    }
}
