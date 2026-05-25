package com.boogieton.nadok.global.exception;

import lombok.Getter;

@Getter
public class ImageUploadDebugException extends RuntimeException {
    private final Object debugData;

    public ImageUploadDebugException(Object debugData) {
        super("프로필 이미지 업로드 디버깅 예외");
        this.debugData = debugData;
    }
}