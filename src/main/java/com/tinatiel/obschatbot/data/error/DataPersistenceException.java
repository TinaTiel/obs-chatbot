package com.tinatiel.obschatbot.data.error;

/**
 * Generic catch-all exception that wraps any DAO-related exceptions such as DataIntegrityViolation,
 * ConstraintException, etc.
 */
public class DataPersistenceException extends RuntimeException {

  public DataPersistenceException(Object object, Throwable cause) {
    super("Unable to persist object " + object, cause);
  }

  public DataPersistenceException(String message) {
    super(message);
  }

  public DataPersistenceException(String message, Throwable cause) {
    super(message, cause);
  }
}
