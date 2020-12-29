package com.tinatiel.obschatbot.core.error;

public class ServiceNotReadyException extends AbstractException {

    public ServiceNotReadyException(String message, Throwable cause) {
        super(Code.SERVICE_NOT_READY, message, cause);
    }

}
