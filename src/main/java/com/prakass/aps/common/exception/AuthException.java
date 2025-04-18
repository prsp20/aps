package com.prakass.aps.common.exception;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
  public AuthException(String message) {
    super(message);
  }
}
