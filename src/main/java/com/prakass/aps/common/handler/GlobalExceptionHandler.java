package com.prakass.aps.common.handler;

import com.prakass.aps.common.dto.ResponseDto;
import com.prakass.aps.common.dto.ValidationError;
import com.prakass.aps.common.exception.DuplicateEmailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

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
}
