package com.head4work.fileservice.util;

import com.head4work.fileservice.error.CustomResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticatedUser {
    public static String getAuthenticatedUserId() throws CustomResponseException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new CustomResponseException("Authentication is null. No user is logged in.", HttpStatus.UNAUTHORIZED);
        }
        Object principal = authentication.getPrincipal();
        return principal.toString();

    }
}