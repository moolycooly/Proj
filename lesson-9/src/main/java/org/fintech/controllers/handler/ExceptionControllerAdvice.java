package org.fintech.controllers.handler;


import org.fintech.exception.EventNotFoundException;
import org.fintech.exception.PlaceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> methodArgumentNotValid(MethodArgumentNotValidException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Invalid request");

        Map<String, String> errors = new HashMap<>();
        e.getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);

        });
        problemDetail.setProperty("errors",errors);

        return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);

    }
    @ExceptionHandler(PlaceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlePlaceNotFound(PlaceNotFoundException ex) {
        var problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND,ex.getMessage());
        problemDetail.setProperty("error", "Place not found");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(problemDetail);
    }
    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEventNotFound(EventNotFoundException ex) {
        var problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND,ex.getMessage());
        problemDetail.setProperty("error", "Event not found");

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(problemDetail);
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseStatusException handleResponseStatusException(ResponseStatusException ex) {
        return ex;
    }
}
