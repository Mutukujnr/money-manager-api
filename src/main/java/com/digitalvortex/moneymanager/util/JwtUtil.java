package com.digitalvortex.moneymanager.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400}") // Default 24 hours in seconds
    private int jwtExpirationMs;

    @Value("${jwt.refresh.expiration:604800}") // Default 7 days in seconds
    private int jwtRefreshExpirationMs;

    /**
     * Generate JWT token for user email (primary method for your service)
     */
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return generateToken(claims, email);
    }

    /**
     * Generate JWT token for user
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return generateToken(claims, userDetails.getUsername());
    }

    /**
     * Generate JWT token with custom claims
     */
    public String generateToken(Map<String, Object> claims, String subject) {
        return createToken(claims, subject, jwtExpirationMs * 1000L);
    }

    /**
     * Generate refresh token for email
     */
    public String generateRefreshToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createToken(claims, email, jwtRefreshExpirationMs * 1000L);
    }

    /**
     * Create JWT token with specified expiration
     */
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Extract email from token (matches your service logic)
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract username from token (alias for extractEmail for compatibility)
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract expiration date from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract specific claim from token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Check if token is expired
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate token against email (matches your service logic)
     */
    public Boolean validateToken(String token, String email) {
        final String tokenEmail = extractEmail(token);
        return (tokenEmail.equals(email) && !isTokenExpired(token));
    }

    /**
     * Validate token against user details
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Validate token format and signature
     */
    public Boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Check if token is refresh token
     */
    public Boolean isRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return "refresh".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get remaining time until token expiration in milliseconds
     */
    public Long getTokenRemainingTime(String token) {
        Date expiration = extractExpiration(token);
        return expiration.getTime() - System.currentTimeMillis();
    }

    /**
     * Extract custom claim from token
     */
    public Object extractCustomClaim(String token, String claimName) {
        Claims claims = extractAllClaims(token);
        return claims.get(claimName);
    }

    /**
     * Generate token with custom expiration time for email
     */
    public String generateTokenWithExpiration(String email, long expirationMs) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email, expirationMs);
    }

    /**
     * Check if token needs refresh (expires within specified minutes)
     */
    public Boolean needsRefresh(String token, int minutesBeforeExpiry) {
        try {
            long remainingTime = getTokenRemainingTime(token);
            return remainingTime < (minutesBeforeExpiry * 60 * 1000L);
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Get token issue date
     */
    public Date getTokenIssueDate(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    /**
     * Refresh access token using refresh token (returns email-based token)
     */
    public String refreshAccessToken(String refreshToken) {
        if (!isRefreshToken(refreshToken) || isTokenExpired(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String email = extractEmail(refreshToken);
        Map<String, Object> claims = new HashMap<>();
        return generateToken(claims, email);
    }

    /**
     * Get signing key for JWT operations
     */
    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes()); // ✅ uses plain string
    }


    /**
     * Extract token from Authorization header
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * Get token type (access or refresh)
     */
    public String getTokenType(String token) {
        try {
            Object type = extractCustomClaim(token, "type");
            return type != null ? type.toString() : "access";
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * Validate token and return validation details
     */
    public TokenValidationResult validateTokenDetailed(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return new TokenValidationResult(false, "Token is null or empty");
            }

            Claims claims = extractAllClaims(token);

            if (isTokenExpired(token)) {
                return new TokenValidationResult(false, "Token is expired");
            }

            return new TokenValidationResult(true, "Token is valid");

        } catch (ExpiredJwtException e) {
            return new TokenValidationResult(false, "Token is expired");
        } catch (MalformedJwtException e) {
            return new TokenValidationResult(false, "Token is malformed");
        } catch (UnsupportedJwtException e) {
            return new TokenValidationResult(false, "Token is unsupported");
        } catch (IllegalArgumentException e) {
            return new TokenValidationResult(false, "Token claims string is empty");
        } catch (Exception e) {
            return new TokenValidationResult(false, "Token validation failed: " + e.getMessage());
        }
    }

    /**
     * Generate token with additional user claims
     */
    public String generateTokenWithUserClaims(UserDetails userDetails, Map<String, Object> additionalClaims) {
        Map<String, Object> claims = new HashMap<>();
        if (additionalClaims != null) {
            claims.putAll(additionalClaims);
        }
        claims.put("username", userDetails.getUsername());
        claims.put("authorities", userDetails.getAuthorities());
        return generateToken(claims, userDetails.getUsername());
    }

    /**
     * Blacklist token (you would typically store this in Redis or database)
     */
    public Boolean isTokenBlacklisted(String token) {
        // Implementation depends on your blacklist storage strategy
        // This is a placeholder - implement based on your needs
        return false;
    }

    /**
     * Get token expiration time in seconds from now
     */
    public Long getTokenExpirationSeconds(String token) {
        Long remainingMs = getTokenRemainingTime(token);
        return remainingMs > 0 ? remainingMs / 1000 : 0;
    }

    /**
     * Inner class for detailed token validation results
     */
    public static class TokenValidationResult {
        private final boolean valid;
        private final String message;

        public TokenValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return "TokenValidationResult{" +
                    "valid=" + valid +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}