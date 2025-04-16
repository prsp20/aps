package com.prakass.aps.entities.user_account;

import com.prakass.aps.common.base.AbstractEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "user_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserSessionsEntity extends AbstractEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String accessTokenGuid;

  private String refreshTokenGuid;

//  private LocalDateTime expiresAt;

  private boolean revoked;


}
