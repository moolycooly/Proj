package org.fintech.exceptions;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @Value("${cbr.api.retry}")
    private Integer retry;

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseStatusException handleConstraintViolation(ConstraintViolationException e) {
        List<String> errors = new ArrayList<>();
        for(var violation : e.getConstraintViolations()) {
            errors.add(violation.getMessage());
        }
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, errors.toString());
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
    @ExceptionHandler(ServerIsNotAvailableException.class)
    public ResponseEntity<ServerIsNotAvaibale> serverIsNotAvaliable(ServerIsNotAvailableException e) {

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .header(HttpHeaders.RETRY_AFTER, String.valueOf(retry))
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ServerIsNotAvaibale(e.getMessage(),HttpStatus.SERVICE_UNAVAILABLE));


    }
    @ExceptionHandler(ValuteNotFoundException.class)
    public ResponseStatusException valuteNotFound(ValuteNotFoundException e) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());
    }
}
