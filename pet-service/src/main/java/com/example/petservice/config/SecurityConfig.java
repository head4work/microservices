package com.example.petservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize, @PostAuthorize etc.
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomHeaderAuthenticationFilter customHeaderAuthenticationFilter) throws Exception {
        http
                // 1. Disable CSRF as we are using stateless JWTs
                .csrf(AbstractHttpConfigurer::disable)
                // 2. Set session management to stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 3. Add our custom filter *before* the standard Authorization filter
                //    This filter reads headers and sets the SecurityContext
                .addFilterBefore(customHeaderAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 4. Define authorization rules (can also be done with @PreAuthorize)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/service/**").hasRole("ADMIN") // Requires ROLE_ADMIN authority
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN") // Requires ROLE_USER or ROLE_ADMIN
                        .requestMatchers("/public/**", "/actuator/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // Example public endpoints
                        .anyRequest().authenticated() // All other requests need some authentication info present
                );

        return http.build();
    }

    @Bean
    public CustomHeaderAuthenticationFilter customHeaderAuthenticationFilter() {
        return new CustomHeaderAuthenticationFilter();
    }
}