package com.prakass.aps.controller;

import com.prakass.aps.dto.LoginResponse;
import com.prakass.aps.dto.SignUpResponsePayload;
import com.prakass.aps.dto.UserLoginPayload;
import com.prakass.aps.dto.UserSignupPayload;
import com.prakass.aps.security.JwtTokenUtil;
import com.prakass.aps.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;
  private final AuthenticationManager authenticationManager;
  private final JwtTokenUtil jwtTokenUtil;

  @PostMapping("/signup")
  public ResponseEntity<SignUpResponsePayload> signup(
      @Valid @RequestBody UserSignupPayload userSignupPayload) {
    SignUpResponsePayload signUpResponsePayload = authService.signUpUser(userSignupPayload);
    return new ResponseEntity<>(signUpResponsePayload, HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(
          @Valid @RequestBody UserLoginPayload userLoginPayload
  ) {
    var usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken)authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            userLoginPayload.email(), userLoginPayload.password()
    ));
    var userDetails = (UserDetails)usernamePasswordAuthenticationToken.getPrincipal();
    var jwtToken = jwtTokenUtil.generateToken(userDetails);

    return new ResponseEntity<>(new LoginResponse(jwtToken), HttpStatus.OK);
  }
}
