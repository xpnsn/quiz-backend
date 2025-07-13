package com.bohemian.intellect.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler({
            UnauthorizedAccessException.class,
            UserNotFoundException.class,
            ResourceNotFoundException.class
    })
    public ResponseEntity<CustomErrorResponse> handleException(RuntimeException ex) {
        HttpStatus status;
        String title;

        switch (ex) {
            case UserNotFoundException userNotFoundException -> {
                status = HttpStatus.UNAUTHORIZED;
                title = "User Not Found";
            }
            case UnauthorizedAccessException unauthorizedAccessException -> {
                status = HttpStatus.UNAUTHORIZED;
                title = "Unauthorized Access";
            }
            case ResourceNotFoundException resourceNotFoundException -> {
                status = HttpStatus.BAD_REQUEST;
                title = "Invalid ID";
            }
            case null, default -> {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                title = "An Error Occurred";
            }
        }
        CustomErrorResponse response = new CustomErrorResponse(
                title,
                status.value(),
                LocalDateTime.now().toString(),
                Map.of("error", ex.getMessage())
        );

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleException(Exception ex) {
        return new ResponseEntity<>(new CustomErrorResponse(
                "Server Side Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now().toString(),
                Map.of("errors", ex.getMessage())
        ), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}