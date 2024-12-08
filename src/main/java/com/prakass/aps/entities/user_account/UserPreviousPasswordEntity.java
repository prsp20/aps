package com.prakass.aps.entities.user_account;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_previous_password")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPreviousPasswordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String guid;

    @ManyToOne
    @JoinColumn(name = "user_account_id")
    private UserAccountEntity userAccount;

    private String passwordHash;
}
