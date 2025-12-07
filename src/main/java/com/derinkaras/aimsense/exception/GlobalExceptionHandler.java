package com.derinkaras.aimsense.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

}
