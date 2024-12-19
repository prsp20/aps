package com.prakass.aps.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;

@Builder
public record ResponseDto(
    String status,
    String message,
    @JsonProperty("validation_errors") List<ValidationError> validationErrors) {}
