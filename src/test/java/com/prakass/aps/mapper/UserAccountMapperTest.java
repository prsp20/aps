package com.prakass.aps.mapper;

import com.prakass.aps.dto.AuthPayload;
import com.prakass.aps.dto.UserSignupPayload;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class UserAccountMapperTest {
  @Autowired private UserAccountMapper userAccountMapper;

  private UserSignupPayload userSignupPayload;

  @BeforeEach
  void setUpUserAccountMapper() {
    userSignupPayload = new UserSignupPayload();
    userSignupPayload.setFirstName("John");
    userSignupPayload.setLastName("Doe");
    userSignupPayload.setEmail("john@doe.com");
    userSignupPayload.setPassword("password");
  }

  @Test
  void userSignupPayloadToUserAccountEntityShouldConvertUserSignupPayloadToUserAccountEntity() {
    UserAccountEntity mappedUserAccountEntity =
        userAccountMapper.userSignupPayloadToUserAccountEntity(userSignupPayload);

    Assertions.assertThat(mappedUserAccountEntity).isNotNull();
    Assertions.assertThat(mappedUserAccountEntity.getFirstName())
        .isEqualTo(userSignupPayload.getFirstName());
    Assertions.assertThat(mappedUserAccountEntity.getLastName())
        .isEqualTo(userSignupPayload.getLastName());
    Assertions.assertThat(mappedUserAccountEntity.getEmail())
        .isEqualTo(userSignupPayload.getEmail());
  }

  @Test
  void userSignUpPayloadToUserAccountEntityShouldIgnorePassword() {

    UserAccountEntity mappedUserAccountEntity =
        userAccountMapper.userSignupPayloadToUserAccountEntity(userSignupPayload);

    Assertions.assertThat(mappedUserAccountEntity.getPasswordHash()).isNull();
  }

  @Test
  void userSignUpPayloadToUserAccountEntityShouldGenerateUniqueGuid() {
    UserAccountEntity mappedUserAccountEntity =
        userAccountMapper.userSignupPayloadToUserAccountEntity(userSignupPayload);

    UserAccountEntity secondMappedUserAccountEntity =
        userAccountMapper.userSignupPayloadToUserAccountEntity(userSignupPayload);

    Assertions.assertThat(mappedUserAccountEntity.getGuid())
        .isNotEqualTo(secondMappedUserAccountEntity.getGuid());
  }

  @Test
  void userSignUpPayloadToUserAccountEntityShouldGenerateCurrentTimestampForCreatedAt() {
    UserAccountEntity mappedUserAccountEntity =
        userAccountMapper.userSignupPayloadToUserAccountEntity(userSignupPayload);

    LocalDateTime timestamp = LocalDateTime.now();

    int timeDifference = timestamp.compareTo(mappedUserAccountEntity.getCreatedAt());

    Assertions.assertThat(timeDifference).isLessThan(2);
  }

  @Test
  void userAccountEntityToAuthPayloadShouldConvertUserAccountEntityToAuthPayload() {
    UserAccountEntity userAccountEntity = new UserAccountEntity();
    userAccountEntity.setFirstName("John");
    userAccountEntity.setLastName("Doe");
    userAccountEntity.setEmail("john@doe.com");
    userAccountEntity.setGuid("random-unique-guid");
    userAccountEntity.setCreatedAt(LocalDateTime.now());

    AuthPayload authPayload = userAccountMapper.userAccountEntityToAuthPayload(userAccountEntity);

    Assertions.assertThat(authPayload).isNotNull();
    Assertions.assertThat(authPayload.getFirstName()).isEqualTo(userAccountEntity.getFirstName());
    Assertions.assertThat(authPayload.getLastName()).isEqualTo(userAccountEntity.getLastName());
    Assertions.assertThat(authPayload.getEmail()).isEqualTo(userAccountEntity.getEmail());
    Assertions.assertThat(authPayload.getCreatedAt()).isEqualTo(userAccountEntity.getCreatedAt());
    Assertions.assertThat(authPayload.getGuid()).isEqualTo(userAccountEntity.getGuid());
  }
}
