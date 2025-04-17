package com.prakass.aps.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PasswordRequestPayload(@NotNull(message = "Token cannot be null")
                                     String token,
                                     @NotNull(message = "Password cannot be null")
                                     @NotBlank(message = "Password cannot be blank")
                                     String newPassword,
                                     @NotNull(message = "Password confirmation cannot be null")
                                     @NotBlank(message = "Password confirmation cannot be blank")
                                     String confirmPassword) {
}
