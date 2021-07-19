package com.tinatiel.obschatbot.web;

import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalControllerErrorAdvice extends ResponseEntityExceptionHandler {

  private final DefaultErrorAttributes defaultErrorAttributes;

  @ExceptionHandler({IllegalArgumentException.class, DataPersistenceException.class})
  protected ResponseEntity<Object> handle(Exception ex, WebRequest webRequest) throws Exception {

    Map<String, Object> body = defaultErrorAttributes.getErrorAttributes(
      webRequest, ErrorAttributeOptions.defaults());

    body.put("status", 400);
    body.put("error", ex.getClass().getSimpleName());
    body.put("message", ex.getMessage());

    return handleExceptionInternal(
      ex,
      body,
      new HttpHeaders(),
      HttpStatus.BAD_REQUEST,
      webRequest);
  }

}
