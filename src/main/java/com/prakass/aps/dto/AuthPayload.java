package com.prakass.aps.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AuthPayload(
    String guid, String email, String firstName, String lastName, LocalDateTime createdAt) {}
