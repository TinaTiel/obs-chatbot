package com.tinatiel.obschatbot.commandservice.service;

public class CyclicalReferenceException extends RuntimeException {

  public CyclicalReferenceException(String message) {
    super(message);
  }

  public CyclicalReferenceException(String message, Throwable cause) {
    super(message, cause);
  }
}
