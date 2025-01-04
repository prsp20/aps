package com.prakass.aps.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record UserLoginPayload(
    @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
    @NotEmpty(message = "Password is required") String password) {}
