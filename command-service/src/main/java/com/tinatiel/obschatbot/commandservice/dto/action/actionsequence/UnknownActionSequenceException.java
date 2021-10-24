package com.tinatiel.obschatbot.commandservice.dto.action.actionsequence;

public class UnknownActionSequenceException extends RuntimeException {

  public UnknownActionSequenceException(String message) {
    super(message);
  }

  public UnknownActionSequenceException(String message, Throwable cause) {
    super(message, cause);
  }

}
