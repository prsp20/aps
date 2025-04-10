package com.prakass.aps.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record SignUpResponsePayload(
    String guid, String email, String firstName, String lastName, LocalDateTime createdAt) {}
