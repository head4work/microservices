package com.head4work.payrollservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomHeaderAuthenticationFilter extends OncePerRequestFilter {

    // Use constants or @Value annotations for header names
    private static final String USER_ID_HEADER = "X-User-ID";
    private static final String USER_ROLES_HEADER = "X-User-Roles";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String userId = request.getHeader(USER_ID_HEADER);
        String rolesHeader = request.getHeader(USER_ROLES_HEADER);

        if (userId != null && !userId.isEmpty() && rolesHeader != null) {
            System.out.println("Payroll-service Filter: Received User-ID: " + userId + ", Roles: " + rolesHeader); // Logging

            // Split roles string (e.g., "ADMIN,USER") into authorities
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            if (!rolesHeader.trim().isEmpty()) {
                authorities = Stream.of(rolesHeader.split(","))
                        .map(String::trim)
                        .filter(role -> !role.isEmpty())
                        .map(role -> new SimpleGrantedAuthority(role.toUpperCase())) // Add ROLE_ prefix
                        .collect(Collectors.toList());
            }
// update
            // Create Authentication object
            // Principal = userId, Credentials = null (already authenticated by gateway), Authorities = roles
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId, null, authorities);

            // Set the Authentication object in the SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("Payroll-service: SecurityContext populated for user: " + userId + " with authorities: " + authorities); // Logging

        } else {
            System.out.println("Payroll-service: No/incomplete user headers found (" + USER_ID_HEADER + ", " + USER_ROLES_HEADER + "). Clearing SecurityContext."); // Logging
            // Clear context if headers are missing/incomplete ensure no stale auth state
            SecurityContextHolder.clearContext();
        }

        // Continue the filter chain

        filterChain.doFilter(request, response);
    }
}