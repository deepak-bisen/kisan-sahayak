package com.kisan.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for Kisan Marketplace
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles DTO validation errors (e.g., @NotBlank, @Positive).
     */
     @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex){
         Map<String, String> errors = new HashMap<>();
         ex.getBindingResult().getAllErrors().forEach((error) -> {
             String fieldName = ((FieldError) error).getField();
             String errorMessage = error.getDefaultMessage();
             errors.put(fieldName,errorMessage);
         });
         return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
     }

    /**
     * Handles business logic and Feign Client exceptions.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex){
        Map<String, String> error = new HashMap<>();
        error.put("error",ex.getMessage());
        error.put("status", "400");
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all other unexpected system exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex){
        Map<String, String> error = new HashMap<>();
        error.put("error", "An unexpected error occurred in the Marketplace Service");
        error.put("details",ex.getMessage());
        return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
