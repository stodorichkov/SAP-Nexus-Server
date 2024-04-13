package com.example.nexus.exception;

import com.example.nexus.constant.MessageConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handle(NotFoundException ex) {
        log.error(ex.getMessage());
        log.info(ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handle(UserAlreadyExistsException ex) {
        log.error(ex.getMessage());
        log.info(ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler({UnauthorizedException.class, AuthenticationException.class})
    public ResponseEntity<String> handle(Exception ex) {
        log.error(ex.getMessage());
        log.info(ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handle(BadRequestException ex) {
        log.error(ex.getMessage());
        log.info(ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handle(MethodArgumentNotValidException ex) {
        log.error(ex.toString());
        log.info(ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex
                        .getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.joining("\n"))
                );
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<String> handle(FileUploadException ex) {
        log.error(ex.getMessage());
        log.info(ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ex.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handle(IOException ex) {
        log.error(ex.getMessage());
        log.info(ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<String> handle(PaymentException ex) {
        log.error(ex.getMessage());
        log.info(ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        log.error(ex.getMessage());
        log.info(ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MessageConstants.INTERNAL_SERVER_ERROR);
    }
}