package com.gachi.server.global.exception;

import lombok.Getter;

@Getter
public class GachiException extends RuntimeException {

    private final ErrorCode errorCode;

    public GachiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
