package com.prakass.aps.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prakass.aps.dto.*;
import com.prakass.aps.security.SecurityConfig;
import com.prakass.aps.service.AuthService;
import java.time.LocalDateTime;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {
  @Autowired private MockMvc mockMvc;

  @MockitoBean private AuthService authService;

  @MockitoBean private AuthenticationManager authenticationManager;

  @MockitoBean private AuthenticationProvider mockAuthenticationProvider;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  void signUpShouldReturnIsCreatedStatus() throws Exception {
    UserSignupPayload userSignupPayload =
        UserSignupPayload.builder()
            .email("john-doe@mail.com")
            .password("password")
            .firstName("John")
            .lastName("Doe")
            .build();

    LocalDateTime now = LocalDateTime.of(2024, 12, 16, 23, 15, 20);

    SignUpResponsePayload signUpResponsePayload =
        SignUpResponsePayload.builder()
            .email(userSignupPayload.email())
            .firstName(userSignupPayload.firstName())
            .lastName(userSignupPayload.lastName())
            .createdAt(now)
            .guid("unique-guid")
            .build();

    when(authService.signUpUser(userSignupPayload)).thenReturn(signUpResponsePayload);

    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userSignupPayload)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.firstName").value(signUpResponsePayload.firstName()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.lastName").value(signUpResponsePayload.lastName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.guid").value(signUpResponsePayload.guid()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(signUpResponsePayload.email()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.createdAt")
                .value(equalTo(signUpResponsePayload.createdAt().toString())));
  }

  @Test
  void signUpShouldRejectInvalidEmail() throws Exception {
    UserSignupPayload userSignupPayload =
        UserSignupPayload.builder()
            .email("john-doe.com")
            .password("password")
            .firstName("John")
            .lastName("Doe")
            .build();

    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userSignupPayload)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Invalid email format")));
  }

  @Test
  void signUpShouldReturnValidationMessagesOnInvalidPayload() throws Exception {
    UserSignupPayload userSignupPayload = UserSignupPayload.builder().build();
    String[] expectedMessages = {
      "Password is required",
      "Email is required",
      "First name is required",
      "Last name is required",
    };
    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userSignupPayload)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.validation_errors[*].message")
                .value(Matchers.hasItems(expectedMessages)));
  }

  @Test
  void loginShouldReturnOkStatusWithResponseLoginResponsePayload() throws Exception {
    UserLoginPayload userLoginPayload =
        UserLoginPayload.builder().email("john-doe@mail.com").password("password").build();

    UserDetails user =
        User.builder()
            .username("john-doe")
            .password("encoded-password")
            .authorities(new SimpleGrantedAuthority("USER"))
            .build();

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));

    LoginResponse userLoginResponse =
        LoginResponse.builder().accessToken("accessToken").refreshToken("refreshToken").build();

    when(authService.getUserLoginResponse(user)).thenReturn(userLoginResponse);

    when(authService.loginUserAndCreateSessionToken(user))
        .thenReturn(userLoginResponse.refreshToken());

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginPayload)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.accessToken").value(userLoginResponse.accessToken()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.refreshToken")
                .value(userLoginResponse.refreshToken()));
  }

  @Test
  void loginShouldReturnValidationMessagesOnInvalidPayload() throws Exception {
    UserLoginPayload userLoginPayload = UserLoginPayload.builder().build();
    String[] expectedMessages = {
      "Password is required", "Email is required",
    };

    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginPayload)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.validation_errors[*].message")
                .value(Matchers.hasItems(expectedMessages)));
  }

  @Test
  void testResetPassword_ReturnsOk() throws Exception {
    // Arrange
    PasswordRequestPayload payload = new PasswordRequestPayload("random-token", "New", "New");

    // Act & Assert
    mockMvc
        .perform(
            post("/api/v1/auth/forget-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isOk())
        .andExpect(content().string("Password successfully updated"));

    verify(authService, times(1)).resetPassword(any(PasswordRequestPayload.class));
  }

  @Test
  void testResetPassword_ReturnsValidationMessagesOnInvalidPayload() throws Exception {
    PasswordRequestPayload payload = new PasswordRequestPayload(null, "New", "New");
    String[] expectedMessages = {"Token cannot be null"};
    mockMvc
        .perform(
            post("/api/v1/auth/forget-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.validation_errors[*].message")
                .value(Matchers.hasItems(expectedMessages)));
  }
}
