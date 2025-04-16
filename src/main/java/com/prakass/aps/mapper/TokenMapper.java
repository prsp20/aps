package com.prakass.aps.mapper;

import com.prakass.aps.entities.token.AccessToken;
import com.prakass.aps.entities.token.RefreshToken;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface TokenMapper {

    @Mapping(target = "accessToken", source = "token")
    @Mapping(target = "expired", constant = "false")
    @Mapping(target = "userAccountEntity", source = "userAccountEntity")
    AccessToken accessTokenToEntity(String token, UserAccountEntity userAccountEntity);

    @Mapping(target = "refreshToken", source = "token")
    @Mapping(target = "expired", constant = "false")
    @Mapping(target = "userAccountEntity", source = "userAccountEntity")
    RefreshToken refreshTokenEntity(String token, UserAccountEntity userAccountEntity);

}
