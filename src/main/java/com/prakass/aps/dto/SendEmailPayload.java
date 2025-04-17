package com.prakass.aps.dto;

import jakarta.validation.constraints.NotNull;

public record SendEmailPayload(@NotNull(message = "Email Cannot be null") String email) {
}
