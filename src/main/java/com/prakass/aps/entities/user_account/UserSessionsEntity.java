package com.prakass.aps.entities.user_account;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionsEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String guid;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserAccountEntity userAccount;

  private String jwtToken;
  private LocalDateTime createdAt;
  private LocalDateTime expiresAt;
  private boolean revoked;
}
