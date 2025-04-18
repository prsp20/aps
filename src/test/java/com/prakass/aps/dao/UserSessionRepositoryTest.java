package com.prakass.aps.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.prakass.aps.entities.user_account.UserSessionsEntity;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserSessionRepositoryTest {

  @Autowired private UserSessionRepository userSessionRepository;

  @Test
  @DisplayName("Should find UserSession by accessTokenGuid and refreshTokenGuid")
  void findUserSessionsEntitiesByAccessTokenGuidAndRefreshTokenGuid() {
    // Arrange
    String accessToken = "access-123";
    String refreshToken = "refresh-456";
    // Act
    UserSessionsEntity session = new UserSessionsEntity();
    session.setAccessTokenGuid(accessToken);
    session.setRefreshTokenGuid(refreshToken);
    userSessionRepository.save(session);
    Optional<UserSessionsEntity> result =
        userSessionRepository.findUserSessionsEntitiesByRefreshTokenGuid(refreshToken);
    // Assert
    assertThat(result).isPresent();
    assertThat(result.get().getAccessTokenGuid()).isEqualTo(accessToken);
    assertThat(result.get().getRefreshTokenGuid()).isEqualTo(refreshToken);
  }

  @Test
  @DisplayName("Should return empty if no matching session found")
  void shouldReturnEmptyIfNoMatch() {
    // When
    Optional<UserSessionsEntity> result =
        userSessionRepository.findUserSessionsEntitiesByRefreshTokenGuid("none");
    // Then
    assertThat(result).isEmpty();
  }
}
