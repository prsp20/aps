package com.prakass.aps.dto;

import lombok.Builder;

@Builder
public record LoginResponse(String accessToken, String refreshToken) {}
