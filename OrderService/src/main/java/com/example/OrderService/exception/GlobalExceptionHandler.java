package com.example.OrderService.exception;

import com.example.OrderService.response.ErrorResponseDto;
import io.temporal.client.WorkflowFailedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Clock;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Clock clock;

    public GlobalExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return new ResponseEntity<>(ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now(clock))
                .status(HttpStatus.NOT_FOUND.value())
                .errorType("RESOURCE_NOT_FOUND")
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .build(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(InsufficientStockException ex, HttpServletRequest request) {
        return new ResponseEntity<>(ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now(clock))
                .status(HttpStatus.CONFLICT.value())
                .errorType("INSUFFICIENT_STOCK")
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .build(),
                HttpStatus.CONFLICT);
    }


}
