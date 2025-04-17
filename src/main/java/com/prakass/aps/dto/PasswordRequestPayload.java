package com.prakass.aps.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PasswordRequestPayload ( @NotNull(message = "Reset token cannot be null")
                                         String token,
                                         @NotNull(message = "Password cannot be null")
                                         @Pattern(
                                                 regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                                                 message = "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character"
                                         )
                                         String newPassword,
                                         @NotNull(message = "Password confirmation cannot be null")
                                         String confirmPassword) {
}
