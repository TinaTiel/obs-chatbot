package com.tinatiel.obschatbot.web.error;

import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import com.tinatiel.obschatbot.web.error.ErrorResponse.FieldMessage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalControllerErrorAdvice {

  @ExceptionHandler({IllegalArgumentException.class})
  protected ResponseEntity<ErrorResponse> handle(IllegalArgumentException ex, WebRequest webRequest) {

    ErrorResponse errorResponse = errorDefaults(ex, webRequest).status(400).build();

    return handleErrorResponse(errorResponse);

  }

  @ExceptionHandler({DataPersistenceException.class})
  protected ResponseEntity<ErrorResponse> handle(DataPersistenceException ex, WebRequest webRequest) {

    ErrorResponse errorResponse = errorDefaults(ex, webRequest).status(400).build();

    return handleErrorResponse(errorResponse);
  }

  @ExceptionHandler({MethodArgumentNotValidException.class})
  protected ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException ex, WebRequest webRequest) {

    ErrorResponse.ErrorResponseBuilder errorResponseBuilder = errorDefaults(ex, webRequest)
      .message("invalid fields")
      .status(400);

    List<FieldMessage> fieldMessages = new ArrayList<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String field = ((FieldError) error).getField();
      String message = error.getDefaultMessage();
      fieldMessages.add(FieldMessage.builder().field(field).message(message).build());
    });
    fieldMessages.sort(Comparator.comparing(FieldMessage::getField));
    errorResponseBuilder.fields(fieldMessages);

    return handleErrorResponse(errorResponseBuilder.build());

  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  protected ResponseEntity<ErrorResponse> handle(HttpMessageNotReadableException ex, WebRequest webRequest) {

    ErrorResponse errorResponse = errorDefaults(ex, webRequest).status(400).build();

    return handleErrorResponse(errorResponse);
  }

  ErrorResponse.ErrorResponseBuilder errorDefaults(Throwable throwable, WebRequest webRequest) {
    return ErrorResponse.builder()
      .path(webRequest.getContextPath())
      .error(throwable.getClass().getSimpleName())
      .message(throwable.getMessage());
  }

  ResponseEntity<ErrorResponse> handleErrorResponse(ErrorResponse errorResponse) {
    return new ResponseEntity<>(errorResponse, new HttpHeaders(), errorResponse.getStatus());
  }

}
