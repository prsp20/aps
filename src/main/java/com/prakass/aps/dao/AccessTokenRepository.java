package com.prakass.aps.dao;

import com.prakass.aps.entities.token.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {

    Optional<AccessToken> findAccessTokenByAccessToken(String name);
}
