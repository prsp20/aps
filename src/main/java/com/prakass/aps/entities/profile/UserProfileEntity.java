package com.prakass.aps.entities.profile;

import com.prakass.aps.entities.user_account.UserAccountEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String guid;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserAccountEntity userAccount;

  private String profilePicture;
  private LocalDate dateOfBirth;
  private String timezone;
  private String preferredLanguage;

  @ManyToOne
  @JoinColumn(name = "address_id")
  private AddressEntity address;
}
