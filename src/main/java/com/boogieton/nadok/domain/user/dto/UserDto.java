package com.boogieton.nadok.domain.user.dto;

import com.boogieton.nadok.domain.user.entity.Gender;
import com.boogieton.nadok.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

public class UserDto {


    @Getter
    @NoArgsConstructor
    public static class SignupReq {
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        private String password;

        @NotBlank(message = "닉네임은 필수 입력 값입니다.")
        private String nickname;
    }

    @Getter
    @NoArgsConstructor
    public static class LoginReq {
        @NotBlank(message = "이메일을 입력해주세요.")
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        private String password;
    }

    @Getter
    @NoArgsConstructor
    public static class UpdateReq {
        private String password;
        private String nickname;
        private Gender gender;
        private Date birthday;
    }



    @Getter
    @AllArgsConstructor
    public static class CheckAvailableRes {
        private boolean isAvailable; // true: 사용 가능, false: 중복
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ProfileRes {
        private Long userId;
        private String email;
        private String nickname;
        private Gender gender;
        private Date birthday;
        private String profileImgUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // Entity -> DTO 변환 편의 메서드
        public static ProfileRes fromEntity(User user) {
            return ProfileRes.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .gender(user.getGender())
                    .birthday(user.getBirthday())
                    .profileImgUrl(user.getProfileImgUrl())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();
        }
    }
}