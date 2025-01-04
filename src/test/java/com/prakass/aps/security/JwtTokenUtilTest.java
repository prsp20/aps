//package com.prakass.aps.security;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//
//import java.util.HashSet;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//public class JwtTokenUtilTest {
////    JwtTokenUtil jwtTokenUtil = new JwtTokenUtil("secret", 360000L);
//
//    @Mock
//    private Authentication authentication;
//
//    @BeforeEach
//    public void setUp() {
//        Set<String> roles = new HashSet<>();
//        roles.add("USER");
//        when(authentication.getPrincipal()).thenReturn(User.builder()
//                .username("John-doe")
//                .password("random")
//                .authorities(roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())));
//    }
//
//    @Test
//    public void generateTokenShouldReturnValidJwtToken() {
////        jwtTokenUtil.generateToken()
//    }
//
//
//}
