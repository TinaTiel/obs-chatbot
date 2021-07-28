package com.tinatiel.obschatbot.data.error;

import org.springframework.dao.DataIntegrityViolationException;

/**
 * Generic catch-all exception that wraps any DAO-related exceptions such as DataIntegrityViolation,
 * ConstraintException, etc.
 */
public class DataPersistenceException extends RuntimeException {

  public static DataPersistenceException fromObject(Object object, Throwable cause) {
    String message = "Unable to persist object " + object;
    if (cause instanceof DataIntegrityViolationException) {
      if (((DataIntegrityViolationException) cause).getMessage().contains("UNIQUE_NAME")) {
        message = "Name must be unique";
      }
    }
    return new DataPersistenceException(message, cause);
  }

//  public DataPersistenceException(Object object, Throwable cause) {
//    super(message, cause);
//  }

  public DataPersistenceException(String message) {
    super(message);
  }

  public DataPersistenceException(String message, Throwable cause) {
    super(message, cause);
  }
}
