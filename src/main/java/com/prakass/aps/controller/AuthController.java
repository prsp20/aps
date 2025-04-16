package com.prakass.aps.controller;

import com.prakass.aps.dto.*;
import com.prakass.aps.entities.user_account.UserAccountEntity;
import com.prakass.aps.service.AuthService;
import com.prakass.aps.service.TokenService;
import com.prakass.aps.service.UserAccountService;
import com.prakass.aps.utils.JwtToken;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;
  private final AuthenticationManager authenticationManager;
  private final UserAccountService userAccountService;

  @PostMapping("/signup")
  public ResponseEntity<SignUpResponsePayload> signup(
      @Valid @RequestBody UserSignupPayload userSignupPayload) {
    SignUpResponsePayload signUpResponsePayload = authService.signUpUser(userSignupPayload);
    return new ResponseEntity<>(signUpResponsePayload, HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(
      @Valid @RequestBody UserLoginPayload userLoginPayload) {
    UserDetails userDetails =
        (UserDetails)
            authenticationManager
                .authenticate(
                    new UsernamePasswordAuthenticationToken(
                        userLoginPayload.email(), userLoginPayload.password()))
                .getPrincipal();
    LoginResponse userLoginResponse = userAccountService.getUserLoginResponse(userDetails);
    return new ResponseEntity<>(userLoginResponse, HttpStatus.OK);
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenPayload refreshToken) {
    LoginResponse loginResponse = userAccountService.generateRefreshToken(refreshToken);
    return new ResponseEntity<>(loginResponse, HttpStatus.OK);
  }

}
