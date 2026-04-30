package com.example.SecurityService.exception;

import com.example.SecurityService.response.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
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
                "REQUESTED RESOURCE COULD NOT BE FOUND",
                request.getRequestURI(),
                ex.getMessage()
        ), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({AuthenticationException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ErrorResponseDto> handleSecurityExceptions(Exception ex, HttpServletRequest request) {

        HttpStatus status;
        String errorType;

        if (ex instanceof AuthenticationException) {
            status = HttpStatus.UNAUTHORIZED;
            errorType = "UNAUTHORIZED";
        } else if (ex instanceof AuthorizationDeniedException) {
            status = HttpStatus.FORBIDDEN;
            errorType = "FORBIDDEN";
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorType = "INTERNAL_SERVER_ERROR";
        }

        return new ResponseEntity<>(new ErrorResponseDto(
                LocalDateTime.now(clock),
                status.value(),
                errorType,
                request.getRequestURI(),
                ex.getMessage()
        ), status);
    }

    @ExceptionHandler(AccountConflictException.class)
    public ResponseEntity<ErrorResponseDto> handleAccountConflict(AccountConflictException ex, HttpServletRequest request) {
        return new ResponseEntity<>(
                ErrorResponseDto.builder()
                        .timestamp(LocalDateTime.now(clock))
                        .status(HttpStatus.CONFLICT.value())
                        .errorType("ACCOUNT CONFLICT")
                        .path(request.getRequestURI())
                        .message(ex.getMessage())
                        .build(),
                HttpStatus.CONFLICT);
    }

}
