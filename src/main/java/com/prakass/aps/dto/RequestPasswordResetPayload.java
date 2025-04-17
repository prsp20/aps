package com.prakass.aps.dto;

import jakarta.validation.constraints.NotNull;

public record RequestPasswordResetPayload(@NotNull(message = "Email Cannot be null") String email) {
}
