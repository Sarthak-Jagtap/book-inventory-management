package com.bookinventory.user.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils() {}

    /** Returns the userId stored in the JWT (via filter details). */
    public static Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof UsernamePasswordAuthenticationToken token) {
            Object creds = token.getCredentials();   // ← was getDetails(), WRONG
            if (creds instanceof Integer id) return id;
        }
        throw new IllegalStateException("User ID not found in security context");
    }

    /** Returns the authenticated username (JWT subject). */
    public static String getCurrentUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /** Returns the role name without the ROLE_ prefix. */
    public static String getCurrentRoleName() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("Guest");
    }
}