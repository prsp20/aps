package com.prakass.aps.controller;

import com.prakass.aps.dto.*;
import com.prakass.aps.service.AuthService;
import com.prakass.aps.service.UserAccountService;
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
  public ResponseEntity<LoginResponse> refreshToken(
      @RequestBody @Valid RefreshTokenPayload refreshToken) {
    LoginResponse loginResponse = userAccountService.generateRefreshToken(refreshToken);
    return new ResponseEntity<>(loginResponse, HttpStatus.OK);
  }

  @PostMapping("/request-password-reset")
  public ResponseEntity<String> requestPasswordReset(@RequestBody @Valid SendEmailPayload payload) {
    return new ResponseEntity<>(userAccountService.requestPasswordReset(payload), HttpStatus.OK);
  }

  @PostMapping("/forget-password")
  public ResponseEntity<String> resetPassword(@RequestBody @Valid PasswordRequestPayload passwordRequestPayload) {
    userAccountService.resetPassword(passwordRequestPayload);
    return new ResponseEntity<>("Password successfully updated", HttpStatus.OK);
  }

}
