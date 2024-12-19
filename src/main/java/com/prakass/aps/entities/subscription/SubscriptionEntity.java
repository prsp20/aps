package com.prakass.aps.entities.subscription;

import com.prakass.aps.entities.profile.CustomerCompanyProfileEntity;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subscription")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String guid;

  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private boolean active;

  @ManyToOne
  @JoinColumn(name = "subscription_plan_id")
  private ProductEntity product;

  @ManyToOne
  @JoinColumn(name = "user_account_id", nullable = true)
  private UserAccountEntity userAccount;

  @ManyToOne
  @JoinColumn(name = "customer_company_id", nullable = true)
  private CustomerCompanyProfileEntity customerCompany;
}
