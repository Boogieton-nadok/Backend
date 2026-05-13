package com.boogieton.nadok.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class UserDto {

    @Getter
    public static class LoginReq {
        private String username;
        private String password;
    }

    @Getter
    @Builder
    public static class LoginRes{
        private Long userId;
        private String nickname;
        private String email;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
