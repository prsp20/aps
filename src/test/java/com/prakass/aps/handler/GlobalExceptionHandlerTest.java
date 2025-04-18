package com.prakass.aps.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.prakass.aps.common.dto.ResponseDto;
import com.prakass.aps.common.exception.AuthException;
import com.prakass.aps.common.exception.BadRequestException;
import com.prakass.aps.common.exception.ResourceNotFoundException;
import com.prakass.aps.common.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class GlobalExceptionHandlerTest {
  private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

  @Test
  void handleResourceNotFoundException_ShouldReturnNotFoundStatus() {
    // Arrange
    String errorMessage = "Resource not found";
    ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage);

    // Act
    ResponseEntity<ResponseDto> response =
        exceptionHandler.handleResourceNotFoundException(exception);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

    ResponseDto responseBody = response.getBody();
    assertNotNull(responseBody);
    assertEquals(errorMessage, responseBody.message());
  }

  @Test
  void handleAuthException_ShouldReturnUnauthorizedStatus() {
    // Arrange
    String errorMessage = "Authentication failed";
    AuthException exception = new AuthException(errorMessage);

    // Act
    ResponseEntity<ResponseDto> response = exceptionHandler.handleAuthException(exception);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

    ResponseDto responseBody = response.getBody();
    assertNotNull(responseBody);
    assertEquals(errorMessage, responseBody.message());
  }

  @Test
  void handleBadRequestException_ShouldReturnBadRequestStatus() {
    // Arrange
    String errorMessage = "Invalid input";
    BadRequestException exception = new BadRequestException(errorMessage);

    // Act
    ResponseEntity<ResponseDto> response = exceptionHandler.handleBadRequestException(exception);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

    ResponseDto responseBody = response.getBody();
    assertNotNull(responseBody);
    assertEquals(errorMessage, responseBody.message());
  }
}
