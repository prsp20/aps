package com.prakass.aps.dto;

import com.prakass.aps.entities.user_account.UserSessionsEntity;
import lombok.Builder;

@Builder
public record SessionJwtToken(UserSessionsEntity userSessionsEntity, String jwtToken) {}
