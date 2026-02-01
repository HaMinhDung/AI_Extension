package com.extension.AITranslatorExtension.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        Map<String, String> validationErrors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value"
                ));

        logger.warn("Validation error on request {}: {}", request.getDescription(false), validationErrors);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                validationErrors,
                Instant.now().toEpochMilli()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {

        logger.warn("Illegal argument error on request {}: {}", request.getDescription(false), ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                null,
                Instant.now().toEpochMilli()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception ex,
            WebRequest request) {

        logger.error("Unexpected error on request {}: {}", request.getDescription(false), ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                null,
                Instant.now().toEpochMilli()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Error response structure
     */
    public static class ErrorResponse {
        private int status;
        private String message;
        private Map<String, String> errors;
        private long timestamp;

        public ErrorResponse(int status, String message, Map<String, String> errors, long timestamp) {
            this.status = status;
            this.message = message;
            this.errors = errors;
            this.timestamp = timestamp;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Map<String, String> getErrors() {
            return errors;
        }

        public void setErrors(Map<String, String> errors) {
            this.errors = errors;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
