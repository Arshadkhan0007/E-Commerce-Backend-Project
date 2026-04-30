package com.example.InventoryService.exception;

import com.example.InventoryService.response.ErrorResponseDto;
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
        return new ResponseEntity<>(new ErrorResponseDto(
                LocalDateTime.now(clock),
                HttpStatus.NOT_FOUND.value(),
                "RESOURCE_NOT_FOUND",
                request.getRequestURI(),
                ex.getMessage()
        ), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponseDto> handleOutOfStockException(InsufficientStockException ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorResponseDto(
                LocalDateTime.now(clock),
                HttpStatus.CONFLICT.value(),
                "INSUFFICIENT_FUNDS",
                request.getRequestURI(),
                ex.getMessage()
        ), HttpStatus.CONFLICT);
    }

}
