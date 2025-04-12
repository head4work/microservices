package com.example.authservice.exceptions;

import com.example.authservice.dtos.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handle ArithmeticException
    @ExceptionHandler(ArithmeticException.class)
    public ResponseEntity<ErrorResponse> handleArithmeticException(ArithmeticException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Arithmetic error: " + ex.getMessage(), ex);
    }

    // Handle NullPointerException
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Null pointer encountered: " + ex.getMessage(), ex);
    }

    // Handle CustomJwtException
    @ExceptionHandler(CustomJwtException.class)
    public ResponseEntity<ErrorResponse> handleCustomJwtException(CustomJwtException ex) {
        return buildResponse(ex.getStatus(), ex.getMessage(), ex);
    }

    // Handle CustomResponseException
    @ExceptionHandler(CustomResponseException.class)
    public ResponseEntity<ErrorResponse> handleCustomResponseException(CustomResponseException ex) {
        return buildResponse(ex.getHttpStatus(), ex.getMessage(), ex);
    }



    //Handle user not found
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
    }

    // Handle all uncaught exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", ex);
    }

    // Helper method to create a structured response
    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, Exception ex) {
        logger.error(message, ex); // Log the error with stack trace
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message
        );
        return ResponseEntity.status(status).body(errorResponse);
    }
}
