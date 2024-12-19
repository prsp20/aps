package com.prakass.aps.entities.user_account;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String guid;
  private String firstName;
  private String lastName;
  private String email;
  private String username;
  private String passwordHash;
  private String countryCode;
  private String phoneNumber;
  private boolean emailVerified;
  private boolean phoneVerified;
  private String accountStatus;
  private boolean mfaEnabled;
  private int failedLoginAttempts;
  private LocalDateTime lastLogin;
  private LocalDateTime lastPasswordChanged;
  private String passwordResetToken;
  private LocalDateTime passwordResetTokenGenerated;
  private LocalDateTime accountLockedUntil;
  private String securityQuestion;
  private String securityQuestionHash;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;

  @Transient private Set<String> roles = new HashSet<>();
}
