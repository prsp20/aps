package com.prakass.aps.mapper;

import com.prakass.aps.dto.SignUpResponsePayload;
import com.prakass.aps.dto.UserSignupPayload;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserAccountMapperTest {
  @Autowired private UserAccountMapper userAccountMapper;

  private UserSignupPayload userSignupPayload;

  @BeforeEach
  void setUpUserAccountMapper() {
    userSignupPayload =
        UserSignupPayload.builder()
            .email("john@doe.com")
            .password("password")
            .firstName("John")
            .lastName("Doe")
            .build();
  }

  @Test
  void userSignupPayloadToUserAccountEntityShouldConvertUserSignupPayloadToUserAccountEntity() {
    UserAccountEntity mappedUserAccountEntity =
        userAccountMapper.userSignupPayloadToUserAccountEntity(userSignupPayload);

    Assertions.assertThat(mappedUserAccountEntity).isNotNull();
    Assertions.assertThat(mappedUserAccountEntity.getFirstName())
        .isEqualTo(userSignupPayload.firstName());
    Assertions.assertThat(mappedUserAccountEntity.getLastName())
        .isEqualTo(userSignupPayload.lastName());
    Assertions.assertThat(mappedUserAccountEntity.getEmail()).isEqualTo(userSignupPayload.email());
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

    SignUpResponsePayload signUpResponsePayload =
        userAccountMapper.userAccountEntityToSignUpResponsePayload(userAccountEntity);

    Assertions.assertThat(signUpResponsePayload).isNotNull();
    Assertions.assertThat(signUpResponsePayload.firstName())
        .isEqualTo(userAccountEntity.getFirstName());
    Assertions.assertThat(signUpResponsePayload.lastName())
        .isEqualTo(userAccountEntity.getLastName());
    Assertions.assertThat(signUpResponsePayload.email()).isEqualTo(userAccountEntity.getEmail());
    Assertions.assertThat(signUpResponsePayload.createdAt())
        .isEqualTo(userAccountEntity.getCreatedAt());
    Assertions.assertThat(signUpResponsePayload.guid()).isEqualTo(userAccountEntity.getGuid());
  }
}
