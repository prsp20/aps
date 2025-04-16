package com.prakass.aps.dto;

public record LoginResponseWithToken(String accessToken, String refreshToken) {
}
