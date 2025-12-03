package com.stocat.authapi.exception;

import com.stocat.common.exception.ErrorCode;
import com.stocat.common.exception.ErrorDomain;

public enum AuthErrorCode implements ErrorCode {
    INTERNAL_ERROR(ErrorDomain.AUTH.offset(), "서버 에러가 발생했습니다."),

    INVALID_REQUEST(ErrorDomain.AUTH.offset() + 1, "요청값이 올바르지 않습니다."),
    EMAIL_ALREADY_EXISTS(ErrorDomain.AUTH.offset() + 2, "이미 사용 중인 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(ErrorDomain.AUTH.offset() + 3, "이미 사용 중인 닉네임입니다."),
    INVALID_CREDENTIALS(ErrorDomain.AUTH.offset() + 4, "이메일 또는 비밀번호가 올바르지 않습니다."),
    MEMBER_NOT_FOUND(ErrorDomain.AUTH.offset() + 5, "회원 정보를 찾을 수 없습니다.");


    private final int code;
    private final String message;

    AuthErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
