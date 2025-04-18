package com.prakass.aps.common.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String message) {
    super(message);
  }
}
