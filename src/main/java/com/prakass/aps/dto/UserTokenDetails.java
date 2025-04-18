package com.prakass.aps.dto;

import java.util.Set;
import lombok.Builder;

@Builder
public record UserTokenDetails(
    String userName, Set<String> roles, String accessTokenGuid, String refreshTokenGuid) {}
