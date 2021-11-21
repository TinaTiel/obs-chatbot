package com.tinatiel.obschatbot.commandservice.expand;

public class CommandNotFoundException extends RuntimeException {

  public CommandNotFoundException(String message) {
    super(message);
  }

  public CommandNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
