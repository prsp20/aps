package com.prakass.aps.dto;

import lombok.Builder;

@Builder
public record UserPasswordDetails(String username, String passwordType) {}
