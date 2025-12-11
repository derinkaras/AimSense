package com.derinkaras.aimsense.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateResourceException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "DUPLICATE");
        responseBody.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseBody);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "NOT_FOUND");
        responseBody.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOther(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "INTERNAL_ERROR");

        // Don't expose internal details in production
        body.put("message", "An unexpected error occurred check api trace stack");

        // Log the actual error for debugging
        ex.printStackTrace(); // or use a logger

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "VALIDATION_ERROR");
        body.put("message", ex.getBindingResult().getFieldError().getDefaultMessage());
        // Error code 400
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }


}
