package com.boogieton.nadok.global.exception;


import com.boogieton.nadok.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseException extends RuntimeException{
    private BaseResponseCode baseResponseCode;
}
