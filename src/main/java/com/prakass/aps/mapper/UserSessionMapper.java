package com.prakass.aps.mapper;

import com.prakass.aps.entities.user_account.UserSessionsEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserSessionMapper {

//    @Mapping(target = "accessTokenGuid", expression = "java(java.util.UUID.randomUUID().toString())")
//    @Mapping(target = "refreshTokenGuid", expression = "java(java.util.UUID.randomUUID().toString())")
//    @Mapping(target = "revoked", constant = "false")
//    UserSessionsEntity userSessionPayloadToUserSessionsEntity();

}

