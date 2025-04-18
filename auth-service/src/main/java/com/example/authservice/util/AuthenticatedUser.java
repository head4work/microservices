package com.example.authservice.util;
import com.example.authservice.config.AuthorizedUser;
import com.example.authservice.exceptions.CustomResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticatedUser {
    public static AuthorizedUser getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new CustomResponseException("Authentication is null. No user is logged in.", HttpStatus.UNAUTHORIZED);
        }
        Object principal = authentication.getPrincipal();
        //debug
//        System.out.println("Authentication details: " + authentication);
//        System.out.println("Principal: " + principal);
//        System.out.println("Authorities: " + authentication.getAuthorities());
        if (principal instanceof AuthorizedUser) {
            return ((AuthorizedUser) principal);
        }
        throw new RuntimeException("Principal is not of type User. Actual type: " + principal.getClass().getName());
    }
}
