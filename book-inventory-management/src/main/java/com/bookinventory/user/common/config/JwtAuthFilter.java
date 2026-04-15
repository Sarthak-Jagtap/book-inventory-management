package com.bookinventory.user.common.config;

import com.bookinventory.user.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest  request,
                                    HttpServletResponse response,
                                    FilterChain         filterChain)
            throws ServletException, IOException {

        // Step 1: Read Authorization header
        String authHeader = request.getHeader("Authorization");

        // Step 2: No header or not a Bearer token → skip (public endpoints pass through)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: Strip "Bearer " prefix
        String token = authHeader.substring(7);

        // Step 4: Validate token
        if (!jwtUtil.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 5: Extract claims
        String  userName = jwtUtil.extractUserName(token);
        String  roleName = jwtUtil.extractRoleName(token);
        Integer userId   = jwtUtil.extractUserId(token);

        // Step 6: Build Spring Security auth object
        // ROLE_ prefix is what Spring Security expects internally
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userName,
                        userId,       // store userId as credentials — easy to retrieve later
                        Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + roleName))
                );

        // Step 7: Register in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Step 8: Continue
        filterChain.doFilter(request, response);
    }
}