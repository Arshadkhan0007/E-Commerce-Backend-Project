package com.example.SecurityService.security;

import com.example.SecurityService.enums.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final Key secretKey;
    private final Clock clock;

    public JwtUtil(@Value("${security.secret-key}") String secretKey, Clock clock) {
        this.clock = clock;
        byte[] decode = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(decode);
    }

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> customClaims = new HashMap<>();
        customClaims.put("tokenType", TokenType.ACTIVE);
        customClaims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet()));
        return Jwts
                .builder()
                .setClaims(customClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(Instant.now(clock)))
                .setExpiration(Date.from(Instant.now(clock).plus(Duration.ofHours(1))))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> customClaims = new HashMap<>();
        customClaims.put("tokenType", TokenType.REFRESH);
        return Jwts
                .builder()
                .setClaims(customClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(Instant.now(clock)))
                .setExpiration(Date.from(Instant.now(clock).plus(Duration.ofDays(7))))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public TokenType extractTokenType(String token) {
        return TokenType.valueOf(extractClaim(token, claims -> claims.get("tokenType", String.class)));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return (extractClaim(token, Claims::getSubject).equals(userDetails.getUsername()) && isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).after(Date.from(Instant.now(clock)));
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        return claimResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
