package com.tinatiel.obschatbot.core.action.enumerator;

public class CyclicalActionsException extends RuntimeException {
    public CyclicalActionsException(String message, Throwable cause) {
        super(message, cause);
    }
}
