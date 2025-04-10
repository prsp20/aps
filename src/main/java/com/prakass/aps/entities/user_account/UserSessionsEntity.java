package com.prakass.aps.entities.user_account;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "user_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSessionsEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String sessionGuid;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserAccountEntity userAccount;

  private Instant createdAt;
  private Instant expiresAt;
  private boolean revoked;
}
