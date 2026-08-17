package com.micka.lama.istt.controllers;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handle exceptions.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Catch the {@link ConstraintViolationException} into a {@link HttpStatus#BAD_REQUEST}.
     *
     * @param exception The exception.
     * @return The response.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(final ConstraintViolationException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

}
