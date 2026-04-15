package com.bookinventory.user.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    // Build signing key from secret string
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Generate Token — called after successful login
    public String generateToken(String userName,
                                Integer userId,
                                String roleName) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId",   userId);
        claims.put("roleName", roleName);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(
                    new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract all claims from token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract userName
    public String extractUserName(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Extract userId
    public Integer extractUserId(String token) {
        return extractAllClaims(token)
                .get("userId", Integer.class);
    }

    // Extract roleName
    public String extractRoleName(String token) {
        return extractAllClaims(token)
                .get("roleName", String.class);
    }

    // Check if token is expired
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    // Validate token with userName
    public boolean validateToken(String token, String userName) {
        String extractedUserName = extractUserName(token);
        return extractedUserName.equals(userName)
                && !isTokenExpired(token);
    }

    // Check if token is valid (used in filter)
    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}