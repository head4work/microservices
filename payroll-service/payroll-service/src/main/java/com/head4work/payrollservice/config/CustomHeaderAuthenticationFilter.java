package com.head4work.payrollservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(CustomHeaderAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String userId = request.getHeader(USER_ID_HEADER);
        String rolesHeader = request.getHeader(USER_ROLES_HEADER);

        if (userId != null && !userId.isEmpty() && rolesHeader != null) {
            logger.info("Filter: Received User-ID: {} Roles: {}", userId, rolesHeader);
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
            logger.info("SecurityContext populated for user: {} with authorities: {}", userId, authorities);

        } else {
            logger.warn("No/incomplete user headers found ({}, {}). Clearing SecurityContext ", USER_ID_HEADER, USER_ROLES_HEADER);
            // Clear context if headers are missing/incomplete ensure no stale auth state
            SecurityContextHolder.clearContext();
        }

        // Continue the filter chain

        filterChain.doFilter(request, response);
    }
}