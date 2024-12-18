package com.prakass.aps.mapper;

import com.prakass.aps.dto.AuthPayload;
import com.prakass.aps.dto.UserSignupPayload;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserAccountMapper {
    @Mapping(target = "guid", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "username", source = "email")
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    UserAccountEntity userSignupPayloadToUserAccountEntity(UserSignupPayload userSignupPayload);

    AuthPayload userAccountEntityToAuthPayload(UserAccountEntity userAccountEntity);
}