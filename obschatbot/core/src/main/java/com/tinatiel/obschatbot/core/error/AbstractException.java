package com.tinatiel.obschatbot.core.error;

public class AbstractException extends RuntimeException {

    protected Code code;

    protected AbstractException(Code code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
    }

    public Code getCode() {
        return code;
    }

}
