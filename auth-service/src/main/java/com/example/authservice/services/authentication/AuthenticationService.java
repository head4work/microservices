package com.example.authservice.services.authentication;


import com.example.authservice.dtos.auth.AuthenticationRequest;
import com.example.authservice.dtos.auth.AuthenticationResponse;
import com.example.authservice.dtos.auth.JwtResponse;
import com.example.authservice.dtos.auth.RegisterRequest;
import com.example.authservice.entities.Token;
import com.example.authservice.entities.User;
import com.example.authservice.enums.Role;
import com.example.authservice.enums.TokenType;
import com.example.authservice.exceptions.CustomJwtException;
import com.example.authservice.repositories.JpaUserRepository;
import com.example.authservice.repositories.TokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JpaUserRepository jpaUserRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;


    public ResponseEntity<?> register(RegisterRequest request) {
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                //TODO NORMAL ROLES ASSIGN
                .roles(Set.of(Role.ROLE_USER, Role.ROLE_ADMIN))
                .build();
       User savedUser = jpaUserRepository.createUser(user);

        var jwtToken = jwtService.generateToken(user);
        var accessToken = jwtService.generateAccessToken(user);
        saveUserToken(savedUser, jwtToken);

        return getJwtCookieRefreshResponse(jwtToken, accessToken);
    }

    private ResponseEntity<JwtResponse> getJwtCookieRefreshResponse(String jwtToken, String accessToken) {
        // Set refresh token as an HTTP-only cookie
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", jwtToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(jwtExpiration / 1000)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new JwtResponse(accessToken));
    }

    public ResponseEntity<?> authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException e) {
            throw new CustomJwtException(e.getMessage(),e.getCause(),HttpStatus.BAD_REQUEST);
        }
        User user = jpaUserRepository.getByEmail(request.getEmail());
        var jwtToken = jwtService.generateToken(user);
        var accessToken = jwtService.generateAccessToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return getJwtCookieRefreshResponse(jwtToken, accessToken);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            User user = jpaUserRepository.getByEmail(userEmail);

            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public ResponseEntity<?> refresh(String refreshToken) {

        Token storedToken = getStoredToken(refreshToken);

        if (storedToken.revoked || jwtService.isTokenExpired(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is invalid");
        }

        User user = storedToken.getUser();
        String newAccessToken = jwtService.generateAccessToken(user);

        return ResponseEntity.ok()
                .body(new JwtResponse(newAccessToken));
    }

    private Token getStoredToken(String refreshToken) {
        return tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CustomJwtException("Invalid refresh token", HttpStatus.UNAUTHORIZED));
    }

    public ResponseEntity<?> invalidateToken(String refreshToken) {
        User user = getStoredToken(refreshToken).getUser();
        revokeAllUserTokens(user);
        return ResponseEntity.ok("Logged out successfully");
    }
}