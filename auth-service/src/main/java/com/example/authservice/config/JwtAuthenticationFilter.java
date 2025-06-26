package com.example.authservice.config;

import com.example.authservice.exceptions.CustomJwtException;
import com.example.authservice.services.authentication.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/service/v1/auth",
            "/login",
            "/assets",
            "/static",
            "/vite.svg",
            "/favicon.ico"
    );
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver exceptionResolver;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService,
                                   @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.exceptionResolver = exceptionResolver;
    }


    //TODO correct  exception handling

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Skip authentication for certain endpoints (e.g., login, registration)
        String servletPath = request.getServletPath();
        if (isPathExcluded(servletPath) || servletPath.equals("/")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exceptionResolver.resolveException(request, response, null,
                    new CustomJwtException("Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED));
            return;
        }
        jwt = authHeader.substring(7);

        try {
            // Validate the token (.parseSignedClaims() inside method do all the validations)
            userEmail = jwtService.extractUsername(jwt);
            // Authenticate the user if not already authenticated
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (ExpiredJwtException e) {
            exceptionResolver.resolveException(request, response, null, new CustomJwtException("JWT token has expired", e, HttpStatus.UNAUTHORIZED));
            return;
        } catch (JwtException | IllegalArgumentException e) {
            exceptionResolver.resolveException(request, response, null, new CustomJwtException("Invalid JWT token", e, HttpStatus.BAD_REQUEST));
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isPathExcluded(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::contains);
    }
}
