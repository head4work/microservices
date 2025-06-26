package com.example.authservice.controllers.authentication;


import com.example.authservice.dtos.auth.AuthenticationRequest;
import com.example.authservice.dtos.auth.RegisterRequest;
import com.example.authservice.services.authentication.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/service/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request
    ) {
        return service.register(request);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return service.authenticate(request);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @CookieValue("refreshToken") String refreshToken) {
        return service.refresh(refreshToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue("refreshToken") String refreshToken) {
        return service.invalidateToken(refreshToken);  // Mark the token as invalid
    }
}