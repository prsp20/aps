package com.prakass.aps.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class JwtTokenUtilTest {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        userDetails = User.builder()
                .username("John-Doe@gmail.com")
                .password("pass")
                .authorities(roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()))
                .build();
    }

    @Test
    public void generateTokenShouldReturnValidJwtToken() {
        String jwtToken = jwtTokenUtil.generateToken(userDetails);

        assertNotNull(jwtToken);
        assertEquals(3, jwtToken.split("\\.").length);
    }
}
