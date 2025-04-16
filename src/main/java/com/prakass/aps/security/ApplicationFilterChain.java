package com.prakass.aps.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prakass.aps.common.dto.ValidationError;
import com.prakass.aps.common.exception.AuthException;
import com.prakass.aps.common.exception.ResourceNotFoundException;
import com.prakass.aps.dao.AccessTokenRepository;
import com.prakass.aps.entities.token.AccessToken;
import com.prakass.aps.utils.JwtToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
public class ApplicationFilterChain extends OncePerRequestFilter {

    private final JwtToken jwtToken;
    private final AuthUserDetailsService authUserDetailsService;
    private final AccessTokenRepository accessTokenRepository;

    public ApplicationFilterChain(JwtToken jwtToken,
                                  AuthUserDetailsService authUserDetailsService,
                                  AccessTokenRepository accessTokenRepository) {
        this.jwtToken = jwtToken;
        this.authUserDetailsService = authUserDetailsService;
        this.accessTokenRepository = accessTokenRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException {
        try {
            String header = request.getHeader("D-Application");
            if (header != null && header.contains("deal-intel")) {
                String authorization = request.getHeader("Authorization");
                if (authorization == null) {
                    throw new AuthException("Authorization header is null", HttpStatus.UNAUTHORIZED);
                }
                if (!authorization.startsWith("Bearer ")) {
                    throw new AuthException("Invalid Authorization Token", HttpStatus.UNAUTHORIZED);
                }
                final String token = authorization.substring(7);

                AccessToken accessToken = accessTokenRepository.findAccessTokenByToken(token)
                        .orElseThrow(() -> new ResourceNotFoundException("Invalid access token", HttpStatus.BAD_REQUEST));
                if (accessToken.isExpired()) {
                    throw new AuthException("Token is expired", HttpStatus.UNAUTHORIZED);
                }
                final String userName = jwtToken.extractUsername(token);
                UserDetails userDetails = authUserDetailsService.loadUserByUsername(userName);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            ValidationError validationError = null;
            if (e instanceof AuthException) {
                validationError = new ValidationError("Auth", e.getMessage());
            } else if (e instanceof ResourceNotFoundException) {
                validationError = new ValidationError("Resource", e.getMessage());
            } else {
                validationError = new ValidationError("Application Error", e.getMessage());
            }
            ObjectMapper objectMapper = new ObjectMapper();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            objectMapper.writeValue(response.getWriter(), validationError);
        }
    }
}
