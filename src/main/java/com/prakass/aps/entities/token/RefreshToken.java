package com.prakass.aps.entities.token;

import com.prakass.aps.entities.user_account.UserAccountEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", columnDefinition = "text", length = 1000, nullable = false)
    private String  token;

    @Column(name = "expired")
    private boolean expired;


    @ManyToOne
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccountEntity userAccountEntity;


}

