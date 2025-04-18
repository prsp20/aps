package com.prakass.aps.dto;

import java.util.Set;

public record UserTokenDetails(
    String userName, Set<String> roles, String accessTokenGuid, String refreshTokenGuid) {}
