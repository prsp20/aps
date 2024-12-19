package com.prakass.aps.entities.user_account.mfa;

import com.prakass.aps.entities.user_account.UserAccountEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_mfa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserMfaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String guid;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserAccountEntity userAccount;

  @Enumerated(EnumType.STRING)
  private MfaMethod mfaMethod;

  private String mfaSecret;
  private boolean mfaVerified;
  private LocalDateTime mfaLastUsed;
  private int mfaFailedAttempts;
  private LocalDateTime mfaLockedUntil;
}
