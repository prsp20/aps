package com.prakass.aps.controller;

import com.prakass.aps.dto.*;
import com.prakass.aps.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;
  private final AuthenticationManager authenticationManager;

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
    LoginResponse userLoginResponse = authService.login(userDetails);
    return new ResponseEntity<>(userLoginResponse, HttpStatus.OK);
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<LoginResponse> refreshToken(
      @RequestBody @Valid RefreshTokenPayload refreshToken) {
    LoginResponse loginResponse = authService.refreshToken(refreshToken);
    return new ResponseEntity<>(loginResponse, HttpStatus.OK);
  }

  @PostMapping("/request-password-reset")
  public ResponseEntity<String> requestPasswordReset(
      @RequestBody @Valid RequestPasswordResetPayload payload) {
    return new ResponseEntity<>(authService.requestPasswordReset(payload), HttpStatus.OK);
  }

  @PostMapping("/reset-password")
  public ResponseEntity<String> resetPassword(
      @RequestBody @Valid PasswordRequestPayload passwordRequestPayload) {
    authService.resetPassword(passwordRequestPayload);
    return new ResponseEntity<>("Password successfully updated", HttpStatus.OK);
  }
}
