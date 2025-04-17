package com.prakass.aps.common.handler;

import com.prakass.aps.common.dto.ResponseDto;
import com.prakass.aps.common.dto.ValidationError;
import com.prakass.aps.common.exception.AuthException;
import com.prakass.aps.common.exception.BadRequestException;
import com.prakass.aps.common.exception.DuplicateEmailException;
import com.prakass.aps.common.exception.ResourceNotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @ExceptionHandler(DuplicateEmailException.class)
  public ResponseEntity<ResponseDto> handleDuplicateEmailException(DuplicateEmailException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ResponseDto.builder().status("failure").message(e.getMessage()).build());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ResponseDto> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    List<ValidationError> fieldErrors =
        e.getFieldErrors().stream()
            .map(error -> new ValidationError(error.getField(), error.getDefaultMessage()))
            .toList();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body(ResponseDto.builder().status("failure").validationErrors(fieldErrors).build());
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ResponseDto> handleBadCredentialsException(BadCredentialsException e) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .contentType(MediaType.APPLICATION_JSON)
        .body(ResponseDto.builder().status("Unauthorized").message(e.getMessage()).build());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ResponseDto> handleException(Exception e) {
    log.error(e.getMessage());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            ResponseDto.builder()
                .status("failure")
                .message("An unexpected error occurred")
                .build());
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ResponseDto> handleResourceNotFoundException(ResourceNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .contentType(MediaType.APPLICATION_JSON)
        .body(ResponseDto.builder().status("No content").message(e.getMessage()).build());
  }

  @ExceptionHandler(AuthException.class)
  public ResponseEntity<ResponseDto> handleAuthException(AuthException e) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .contentType(MediaType.APPLICATION_JSON)
        .body(ResponseDto.builder().status("Unauthorized").message(e.getMessage()).build());
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ResponseDto> handleBadRequestException(BadRequestException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ResponseDto.builder().status("Bad request").message(e.getMessage()).build());
  }
}
