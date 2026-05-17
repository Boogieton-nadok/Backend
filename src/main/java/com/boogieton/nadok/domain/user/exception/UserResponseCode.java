package com.boogieton.nadok.domain.user.exception;

import com.boogieton.nadok.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
public enum UserResponseCode implements BaseResponseCode {
    SIGNUP_FALSE("USER_400_1", 400, "회원가입에 실패하였습니다."),
    USER_NOT_FOUND("USER_404_1", 404, "유저를 찾을 수 없습니다."),
    LOGIN_FALSE("USER_404_2", 404, "이메일 또는 비밀번호가 일치하지 않습니다."),
    NICKNAME_DUPLICATION("USER_409_1", 409, "이미 존재하는 닉네임입니다."),
    EMAIL_DUPLICATION("USER_409_2", 409, "이미 존재하는 이메일입니다."),
    SIGNUP_SUCCESS("USER_200_1",200, "회원가입이 완료되었습니다."),
    LOGIN_SUCCESS("USER_200_2", 200, "로그인에 성공했습니다."),
    UPDATE_SUCCESS("USER_200_3", 200, "회원 정보 수정 성공"),
    NICKNAME_SUCCESS("USER_200_4", 200, "사용할 수 있는 닉네임입니다."),
    DELETE_SUCCESS("USER_200_5", 200, "회원 탈퇴가 성공적으로 완료되었습니다.");


    private final String code;
    private final int httpStatus;
    private final String message;
}
