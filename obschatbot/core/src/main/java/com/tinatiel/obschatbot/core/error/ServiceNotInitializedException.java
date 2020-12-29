package com.tinatiel.obschatbot.core.error;

public class ServiceNotInitializedException extends RuntimeException {

    public ServiceNotInitializedException(String message, Throwable cause) {
        super(message, cause);
    }

}
