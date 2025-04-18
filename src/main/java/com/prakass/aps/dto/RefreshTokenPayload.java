package com.prakass.aps.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenPayload(
    @NotBlank(message = "Refresh Token can't be blank") String refreshToken) {}
