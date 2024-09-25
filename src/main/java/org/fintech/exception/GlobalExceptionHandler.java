package org.fintech.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseStatusException elementNotFound(NoSuchElementException e) {
    return new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());
  }
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseStatusException methodArgumentNotValid(MethodArgumentNotValidException e) {
    Map<String, String> errors = new HashMap<>();
    e.getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return new ResponseStatusException(HttpStatus.BAD_REQUEST, errors.toString());

  }

}
