package com.example.wanted.exception;

import lombok.Getter;

@Getter
public class PostException extends RuntimeException{
    private final ErrorCode errorCode;

    public PostException(ErrorCode errorCode) {
        super(errorCode.getDetail());
        this.errorCode = errorCode;
    }
}
