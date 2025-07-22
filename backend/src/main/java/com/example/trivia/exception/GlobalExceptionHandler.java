package com.example.trivia.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalState(IllegalStateException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", e.getMessage(),
                        "timestamp", Instant.now()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "Unexpected error",
                        "details", e.getMessage(),
                        "timestamp", Instant.now()
                ));
    }
}
