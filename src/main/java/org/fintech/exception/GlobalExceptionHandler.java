package org.fintech.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BindException.class)
  public ResponseStatusException bindExceptionHandler(BindException e) {
    Map<String, String> errors = new HashMap<>();
    e.getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return new ResponseStatusException(HttpStatus.BAD_REQUEST, errors.toString());

  }

  @ExceptionHandler(ServiceIsNotAvailableException.class)
  public ResponseStatusException serviceIsNotAvailableHandler(ServiceIsNotAvailableException e) {
    return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
  }
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseStatusException responseStatusException(ResponseStatusException e) {
    return e;

  }

}
